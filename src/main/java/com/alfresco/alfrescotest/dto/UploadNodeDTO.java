package com.alfresco.alfrescotest.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadNodeDTO {
    private String fileName;
    private String title= null;
    private String description = null;
    private String relativePath=null;
    private String filePath;

}
