package com.alfresco.alfrescotest.service;

import org.alfresco.core.handler.*;
import org.alfresco.core.model.*;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.*;

@Service
public class AlfrescoService {

    static final Logger LOGGER = LoggerFactory.getLogger(AlfrescoService.class);

    private String contentType = "cm:content"; // Set custom content type
    private Boolean autoRename = true;
    private Boolean majorVersion = true;
    private Boolean versioningEnabled = true;
    private String updateComment = null;
    private String updatedName = null;
    private List<String> include = null;
    private List<String> fields = null;

    private Integer skipCount = 0;
    private Integer maxItems = 100;

    @Autowired
    NodesApi nodesApi;

    @Autowired
    DownloadsApi downloadsApi;

    @Autowired
    VersionsApi versionsApi;

    @Autowired
    CommentsApi commentsApi;

    @Autowired
    PeopleApi peopleApi;


    public PersonEntry createPerson(String username, String pwd, String firstname, String lastname, String email){
        List<String> fields = null;

        PersonBodyCreate personBodyCreate = new PersonBodyCreate();
        personBodyCreate.setId(username);
        personBodyCreate.setPassword(pwd);
        personBodyCreate.setFirstName(firstname);
        personBodyCreate.setLastName(lastname);
        personBodyCreate.setEmail(email);
        PersonEntry person = peopleApi.createPerson(personBodyCreate, fields).getBody();
        LOGGER.info("Created person  {}", person);
        return person;
    }

    public NodeChildAssociationPagingList listFolderContent(String rootNodeId, String relativeFolderPath) {
        Integer skipCount = 0;
        Integer maxItems = 100;
        List<String> include = null;
        List<String> fields = new ArrayList<String>();
        fields.add("id");
        fields.add("name");
        List<String> orderBy = null;
        String where = null;
        Boolean includeSource = false;

        LOGGER.info("Listing folder {}{}", rootNodeId, relativeFolderPath);
        NodeChildAssociationPagingList result = nodesApi.listNodeChildren(rootNodeId, skipCount, maxItems, orderBy, where, include,
                relativeFolderPath, includeSource, fields).getBody().getList();
        for (NodeChildAssociationEntry childNodeAssoc: result.getEntries()) {
            LOGGER.info("Found node [name=" + childNodeAssoc.getEntry().getName() + "]");
        }

        return result;
    }

    /**
     * Upload a file from disk
     */
    public Node uploadFile(String parentFolderId, String fileName, String title, String description, String relativeFolderPath, String filePath) {
        Node fileNode = createFileMetadata(parentFolderId, fileName, title, description, relativeFolderPath);

        // Get the file bytes
        File someFile = new File(filePath);
        byte[] fileData = null;
        try {
            fileData = FileUtils.readFileToByteArray(someFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Add the file node content
        Node updatedFileNode = nodesApi.updateNodeContent(fileNode.getId(),
                fileData, majorVersion, updateComment, updatedName, include, fields).getBody().getEntry();

        LOGGER.info("Created file with content: {}", updatedFileNode.toString());

        return updatedFileNode;
    }

    /**
     * Create a text file
     */
//    public Node createTextFile(String parentFolderId, String fileName, String title, String description, String relativeFolderPath, String textContent) {
//        Node fileNode = createFileMetadata(parentFolderId, fileName, title, description, relativeFolderPath);
//
//        // Add the file node content
//        Node updatedFileNode = nodesApi.updateNodeContent(fileNode.getId(),
//                textContent.getBytes(), majorVersion, updateComment, updatedName, include, fields).getBody().getEntry();
//
//        LOGGER.info("Created file with content: {}", updatedFileNode.toString());
//
//        return updatedFileNode;
//    }


    private Node createFileMetadata(String parentFolderId, String fileName, String title, String description,
                                    String relativeFolderPath) {
        List<String> fileAspects = new ArrayList<String>();
        fileAspects.add("cm:titled");
        Map<String, String> fileProps = new HashMap<>();
        fileProps.put("cm:title", title);
        fileProps.put("cm:description", description);

        NodeBodyCreate nodeBodyCreate = new NodeBodyCreate();
        nodeBodyCreate.setName(fileName);
        nodeBodyCreate.setNodeType(contentType);
        System.out.println(fileAspects);
        nodeBodyCreate.setAspectNames(null);
        nodeBodyCreate.setProperties(fileProps);
        nodeBodyCreate.setRelativePath(relativeFolderPath);

        // Create the file node metadata
        Node fileNode = nodesApi.createNode(parentFolderId, nodeBodyCreate, autoRename, majorVersion, versioningEnabled,
                include, fields).getBody().getEntry();

        return fileNode;
    }

    // Download Nodes
    public Resource getNodeContent(String nodeId) throws IOException {
        // Relevant when using API call from web browser, true is the default
        Boolean attachment = true;
        // Only download if modified since this time, optional
        OffsetDateTime ifModifiedSince = null;
        // The Range header indicates the part of a document that the server should return.
        // Single part request supported, for example: bytes=1-10., optional
        String range = null;

        Resource result = nodesApi.getNodeContent(nodeId, attachment, ifModifiedSince, range).getBody();
        LOGGER.info("Got node {} size: {}", result.getFilename(), result.contentLength());

        return result;
    }

    // Create Folder
    public Node createFolder(String folderName,
                              String folderTitle,
                              String folderDescription,
                              String relativeFolderPath) throws IOException {
        Objects.requireNonNull(folderName);

        String rootPath = "-root-";       // /Company Home
        String folderType = "cm:folder";  // Standard out-of-the-box folder type

        List<String> folderAspects = new ArrayList<String>();
        folderAspects.add("cm:titled");
        Map<String, String> folderProps = new HashMap<>();
        folderProps.put("cm:title", folderTitle);
        folderProps.put("cm:description", folderDescription);

        String nodeId = rootPath; // The id of a node. You can also use one of these well-known aliases: -my-, -shared-, -root-
        NodeBodyCreate nodeBodyCreate = new NodeBodyCreate();
        nodeBodyCreate.setName(folderName);
        nodeBodyCreate.setNodeType(folderType);
        nodeBodyCreate.setAspectNames(folderAspects);
        nodeBodyCreate.setProperties(folderProps);
        nodeBodyCreate.setRelativePath(relativeFolderPath);

        List<String> include = null;
        List<String> fields = null;
        Boolean autoRename = true;
        Boolean majorVersion = true;
        // Should versioning be enabled at all?
        Boolean versioningEnabled = true;

        Node folderNode = nodesApi.createNode(nodeId, nodeBodyCreate, autoRename, majorVersion, versioningEnabled,
                include, fields).getBody().getEntry();
        LOGGER.info("Created new folder: {}", folderNode);

        return folderNode;
    }

    public DownloadEntry createZipDownload(String[] nodeIds) throws InterruptedException {
        List<String> fields = null;

        // Set up POST data with node IDs we want to download
        DownloadBodyCreate downloads = new DownloadBodyCreate();
        for (String nodeId : nodeIds) {
            downloads.addNodeIdsItem(nodeId);
        }

        // First create the download on the server side
        DownloadEntry result = downloadsApi.createDownload(downloads, fields).getBody();

        LOGGER.info("Created ZIP download: {}", result.getEntry().toString());

        // Check the download status
        DownloadEntry download = downloadsApi.getDownload(result.getEntry().getId(), fields).getBody();
        while (!download.getEntry().getStatus().equals(Download.StatusEnum.DONE)) {
            LOGGER.info("Checking ZIP download status: {}", download.getEntry().getStatus());
            Thread.sleep(1000); // do nothing for 1000 milliseconds (1 second)
            download = downloadsApi.getDownload(result.getEntry().getId(), fields).getBody();
        }

        LOGGER.info("ZIP download is READY: {}", result.getEntry().getId());

        return download;
    }

    //Get File/Folder MetaDAta
    public Node getNode(String nodeId,
                         String relativeFolderPath) {
        List<String> include = null;
        List<String> fields = null;

        NodeEntry result = nodesApi.getNode(nodeId, include, relativeFolderPath, fields).getBody();
        LOGGER.info("Got node {}", result.getEntry());
        return result.getEntry();
    }

    //TODO: Update New File Version
    public Node uploadNewFileVersion(String fileNodeId, String filePath) {
        // Get the file bytes
        File someFile = new File(filePath);
        byte[] fileData = null;
        try {
            fileData = FileUtils.readFileToByteArray(someFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Update the file node content
        Node updatedFileNode = nodesApi.updateNodeContent(fileNodeId,
                fileData, majorVersion, updateComment, updatedName, include, fields).getBody().getEntry();

        LOGGER.info("Uploaded new content for file: {}", updatedFileNode.toString());

        return updatedFileNode;
    }

    //Version History of a file
    public VersionPagingList listVersionHistory(String fileNodeId) {
        Integer skipCount = 0;
        Integer maxItems = 100;
        List<String> include = null;
        List<String> fields = null;

        LOGGER.info("Listing versions for file node ID {}", fileNodeId);
        VersionPagingList result = versionsApi.listVersionHistory(fileNodeId, include, fields, skipCount, maxItems).getBody().getList();
        for (VersionEntry versionEntry: result.getEntries()) {
            LOGGER.info("Node version " + versionEntry.getEntry().toString());
        }

        return result;
    }


    public Node updateNode(String nodeId, String newName, Map<String, Object> properties, List<String> aspectNames, PermissionsBody permissionsBody) {

        List<String> include = null;
        List<String> fields = null;

        NodeBodyUpdate nodeBodyUpdate = new NodeBodyUpdate();
        nodeBodyUpdate.setName(newName);
        nodeBodyUpdate.setProperties(properties);
        nodeBodyUpdate.setAspectNames(aspectNames);
        nodeBodyUpdate.setPermissions(permissionsBody);

        NodeEntry result = nodesApi.updateNode(nodeId, nodeBodyUpdate, include, fields).getBody();
        LOGGER.info("Updated node {}", result.getEntry());

        return result.getEntry();
    }

    public Comment createComment(String nodeId, String text) {
        CommentBody commentBody = new CommentBody();
        commentBody.setContent(text);
        Comment comment = commentsApi.createComment(nodeId, commentBody, fields).getBody().getEntry();
        LOGGER.info("{}", comment);
        return comment;
    }

    public CommentPagingList getComments(String nodeId) {
        CommentPagingList comments = commentsApi.listComments(nodeId, skipCount, maxItems, fields).getBody().getList();
        return comments;
    }
    public void deleteComment(String nodeId,String commentId) {
         commentsApi.deleteComment(nodeId,commentId);

    }

    public void deleteNode(String nodeId){
        Boolean permanent = false;
        nodesApi.deleteNode(nodeId, permanent);
    }



}