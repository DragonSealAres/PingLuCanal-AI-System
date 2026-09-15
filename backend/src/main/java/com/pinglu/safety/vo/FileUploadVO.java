package com.pinglu.safety.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "File upload response")
public class FileUploadVO {

    @Schema(description = "Saved file name", example = "7f8e9d6c5b4a4321a123456789abcdef.jpg")
    private String fileName;

    @Schema(description = "Public access URL", example = "/uploads/2026/09/09/7f8e9d6c5b4a4321a123456789abcdef.jpg")
    private String url;
}
