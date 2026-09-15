package com.pinglu.safety.service;

import com.pinglu.safety.vo.FileUploadVO;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    FileUploadVO uploadImage(MultipartFile file);
}
