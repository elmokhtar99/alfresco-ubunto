package com.alfresco.alfrescotest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.alfresco.core.model.PermissionsBody;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateNodeDTO {
    private String nodeName;
    private Map<String, Object> properties;
    private List<String> aspectNames;
    private PermissionsBody permissionsBody;

}
