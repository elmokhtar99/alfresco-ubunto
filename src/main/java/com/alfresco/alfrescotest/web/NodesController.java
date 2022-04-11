package com.alfresco.alfrescotest.web;


import com.alfresco.alfrescotest.dto.*;
import com.alfresco.alfrescotest.service.AlfrescoService;
import org.alfresco.core.model.*;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/nodes")
public class NodesController {


    private AlfrescoService alfrescoService;

    public NodesController(AlfrescoService alfrescoService) {
        this.alfrescoService = alfrescoService;
    }

    @PostMapping("/download/{idnode}")
    public void download(@PathVariable("idnode") String idnode) {
        try {
            Resource nodeContent = alfrescoService.getNodeContent(idnode);
            String fileName = nodeContent.getFilename();
            File targetFile = new File("C:\\tools/" + fileName);
            FileUtils.copyInputStreamToFile(nodeContent.getInputStream(), targetFile);
        } catch (Exception e) {
            System.out.println("Error during Downloading the File");
        }
    }

    @PostMapping("/upload/{idnode}")
    public Node upload(@PathVariable("idnode") String idnode, @RequestBody UploadNodeDTO uploadNodeDTO) {
        System.out.println(uploadNodeDTO.getFilePath());
        try {
            Node newFile = alfrescoService.uploadFile(idnode, uploadNodeDTO.getFileName(),
                    uploadNodeDTO.getTitle(), uploadNodeDTO.getDescription(), uploadNodeDTO.getRelativePath(), uploadNodeDTO.getFilePath());
            return newFile;
        }catch (Exception e){
            System.out.println("Problem Occured While uploading file");
            return null;
        }
    }

    @GetMapping("/folders/{idnode}")
    public NodeChildAssociationPagingList getItems(@PathVariable("idnode") String idNode, @RequestBody RelativePathDTO relativePath) {
        try {
            NodeChildAssociationPagingList nodes = alfrescoService.listFolderContent(idNode, relativePath.getRelativePath());
            return nodes;
        }catch (Exception e){
            System.out.println("Problem Occured While Fetching  files");
            return null;
        }
    }

    /**
     * This Method is for Folders Only
     */
    @GetMapping("/metadata/{idnode}")
    public Node getNode(@PathVariable("idnode") String idNode, @RequestBody RelativePathDTO relativePath) {
        Node node = alfrescoService.getNode(idNode,relativePath.getRelativePath());
        return node;
    }

    @PostMapping("/folders/create/")
    public Node createFolder(@RequestBody CreateFolder createFolder){
        Node folderInFolder1 = null;
        try {
            folderInFolder1 = alfrescoService.createFolder(createFolder.getFolderName(), createFolder.getTitle(), createFolder.getDescription(), createFolder.getRelativePath());
        } catch (IOException e) {
            System.out.println("Error Occured While creating Folder ");
        }
        return folderInFolder1;
    }

    @GetMapping("/version/{idNode}")
    public VersionPagingList getVersionofNode(@PathVariable("idNode") String idNode) {
        VersionPagingList nodes = alfrescoService.listVersionHistory(idNode);
        return nodes;
    }

    @GetMapping("/download")
    public void downloadMultiplefile(@RequestBody DownloadZipDTO downloadZipDTO) {
        DownloadEntry downloadEntry = null;
        System.out.println(downloadZipDTO.getIdNodes().toString());
        try {
            downloadEntry = alfrescoService.createZipDownload(downloadZipDTO.getIdNodes());
            Resource zipNodeContent = alfrescoService.getNodeContent(downloadEntry.getEntry().getId());
            File targetFile = new File("C:\\tools/test-step.zip");
            FileUtils.copyInputStreamToFile(zipNodeContent.getInputStream(), targetFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PutMapping ("/update/{idNode}")
    public Node updateNode(@PathVariable("idNode") String idNode, @RequestBody UpdateNodeDTO updateNodeDTO) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("cm:title", "UPDATED title");
        properties.put("cm:description", "UPDATED description");
        Node node = alfrescoService.updateNode(idNode, updateNodeDTO.getNodeName(),
                updateNodeDTO.getProperties(), updateNodeDTO.getAspectNames(), updateNodeDTO.getPermissionsBody());
        return node;
    }

    @DeleteMapping("/delete/{idNode}")
    public void deleteNode(@PathVariable("idNode") String idNode){
        alfrescoService.deleteNode(idNode);
    }

    /**
     * COMMENT MANAGEMENT
     *
     */
    @PostMapping("/comment/{idNode}")
    public Comment createComment(@PathVariable("idNode") String idNode,@RequestBody CommentDTO commentDTO){
        Comment comment = alfrescoService.createComment(idNode, commentDTO.getText());
        return comment;
    }

    @GetMapping("/comment/{idNode}")
    public CommentPagingList getComment(@PathVariable("idNode") String idNode){
        return alfrescoService.getComments(idNode);
    }

    @DeleteMapping("/comment/{idNode}/{idComment}")
    public void deleteComment(@PathVariable("idNode") String idNode,@PathVariable("idComment") String idComment){
        alfrescoService.deleteComment(idNode,idComment);
    }

    @PostMapping("/permis")
    public PermissionsBody getper(@RequestBody PermissionsBody permissionsBody){
        return permissionsBody;
    }







}
