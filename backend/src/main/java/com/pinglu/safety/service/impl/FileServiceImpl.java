package com.pinglu.safety.service.impl;

import com.pinglu.safety.exception.BusinessException;
import com.pinglu.safety.service.FileService;
import com.pinglu.safety.vo.FileUploadVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    private static final long MAX_IMAGE_SIZE = 10L * 1024 * 1024;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
    private static final DateTimeFormatter DATE_PATH_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    @Value("${file.upload.root-path:uploads}")
    private String uploadRootPath;

    @Override
    public FileUploadVO uploadImage(MultipartFile file) {
        validateImage(file);

        String extension = resolveImageExtension(file);
        String fileName = UUID.randomUUID().toString().replace("-", "") + "." + extension;
        String datePath = LocalDate.now().format(DATE_PATH_FORMATTER);
        Path uploadDirectory = Paths.get(uploadRootPath, datePath.split("/")).toAbsolutePath().normalize();
        Path targetFile = uploadDirectory.resolve(fileName).normalize();

        try {
            Files.createDirectories(uploadDirectory);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ex) {
            throw new BusinessException(500, "图片保存失败，请稍后重试");
        }

        String url = "/uploads/" + datePath + "/" + fileName;
        return new FileUploadVO(fileName, url);
    }

    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "上传文件不能为空");
        }
        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new BusinessException(400, "图片大小不能超过10MB");
        }

        String extension = resolveImageExtension(file);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BusinessException(400, "文件类型不合法，仅支持jpg、jpeg、png、webp");
        }
    }

    private String resolveImageExtension(MultipartFile file) {
        String extension = getExtension(file.getOriginalFilename());
        if (ALLOWED_EXTENSIONS.contains(extension)) {
            return extension;
        }

        String contentTypeExtension = getExtensionFromContentType(file.getContentType());
        if (ALLOWED_EXTENSIONS.contains(contentTypeExtension)) {
            return contentTypeExtension;
        }

        String magicExtension = getExtensionFromMagicBytes(file);
        if (ALLOWED_EXTENSIONS.contains(magicExtension)) {
            return magicExtension;
        }

        return "";
    }

    private String getExtensionFromContentType(String contentType) {
        if (!StringUtils.hasText(contentType)) {
            return "";
        }
        return switch (contentType.toLowerCase(Locale.ROOT)) {
            case "image/jpeg", "image/jpg" -> "jpg";
            case "image/png" -> "png";
            case "image/webp" -> "webp";
            default -> "";
        };
    }

    private String getExtensionFromMagicBytes(MultipartFile file) {
        byte[] header = new byte[12];
        int length;
        try (InputStream inputStream = file.getInputStream()) {
            length = inputStream.read(header);
        } catch (IOException ex) {
            return "";
        }

        if (length >= 3
                && (header[0] & 0xFF) == 0xFF
                && (header[1] & 0xFF) == 0xD8
                && (header[2] & 0xFF) == 0xFF) {
            return "jpg";
        }
        if (length >= 8
                && (header[0] & 0xFF) == 0x89
                && header[1] == 0x50
                && header[2] == 0x4E
                && header[3] == 0x47
                && header[4] == 0x0D
                && header[5] == 0x0A
                && header[6] == 0x1A
                && header[7] == 0x0A) {
            return "png";
        }
        if (length >= 12
                && header[0] == 0x52
                && header[1] == 0x49
                && header[2] == 0x46
                && header[3] == 0x46
                && header[8] == 0x57
                && header[9] == 0x45
                && header[10] == 0x42
                && header[11] == 0x50) {
            return "webp";
        }

        return "";
    }

    private String getExtension(String originalFilename) {
        String filename = StringUtils.cleanPath(originalFilename == null ? "" : originalFilename);
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
    }
}
