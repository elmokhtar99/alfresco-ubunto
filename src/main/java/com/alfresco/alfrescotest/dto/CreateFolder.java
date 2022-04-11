package com.alfresco.alfrescotest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateFolder {
    private String folderName;
    private String title= null;
    private String description = null;
    private String relativePath=null;

}
