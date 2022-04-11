package com.alfresco.alfrescotest;

import com.alfresco.alfrescotest.service.AlfrescoService;
import org.alfresco.core.handler.NodesApi;
import org.alfresco.core.model.DownloadEntry;
import org.alfresco.core.model.Node;
import org.alfresco.core.model.NodeChildAssociationPagingList;
import org.alfresco.core.model.VersionPagingList;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.Resource;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class AlfrescotestApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(AlfrescotestApplication.class, args);
	}

	@Autowired
	NodesApi nodesApi;
	@Autowired
	AlfrescoService alfrescoService;

	@Override
	public void run(String... args) throws Exception {
//		Node node =nodesApi.getNode("0b8f59a5-5881-4f8e-8c77-30f5a3724363",null,null,null).getBody().getEntry();
//		System.out.println(node.getName());
//		Node newFile = alfrescoService.uploadFile("df626d1e-55f5-408d-aed6-902f465460b8", "somepicture.png", "PicturefileTitle",
//				"PicturefileDesc", "/This is my Second Folder API", "eerd.PNG");
//		Node newTextFile = alfrescoService.createTextFile("df626d1e-55f5-408d-aed6-902f465460b8", "somestuff.txt", "TextfileTitle",
//				"TextfileDesc", null, "Some text for the file");

//		Resource nodeContent = alfrescoService.getNodeContent("9153db1a-4ae1-4c85-87a1-0cb77ac21f7a");
//
//		// Write file to disk
//		File targetFile = new File("C:\\tools/image.png");
//		FileUtils.copyInputStreamToFile(nodeContent.getInputStream(), targetFile);

//		NodeChildAssociationPagingList nodes = alfrescoService.listFolderContent("-root-", "/This is my First Folder API");
//		System.out.println(nodes.toString());
//		Node folderInFolder1 = alfrescoService.createFolder("Folder Test", "Title2", "Desc2", "/Folder1");

//		Node node = alfrescoService.getNode("df626d1e-55f5-408d-aed6-902f465460b8","");
//		System.out.println(node);

//		VersionPagingList nodes = alfrescoService.listVersionHistory("901b10be-2a91-48f4-9cd8-dd9919b41079");

//		String[] nodeIds = {"901b10be-2a91-48f4-9cd8-dd9919b41079", "874e1256-05e6-4652-b427-8f3d0fcd9fc5"} ;
//
//		DownloadEntry downloadEntry = alfrescoService.createZipDownload(nodeIds);
//		Resource zipNodeContent = alfrescoService.getNodeContent(downloadEntry.getEntry().getId());
//
//		// Write ZIP file to disk
//		File targetFile = new File("C:\\tools/test-step.zip");
//		FileUtils.copyInputStreamToFile(zipNodeContent.getInputStream(), targetFile);
		//alfrescoService.createPerson("user2","123","User1","Test","use@r.com");

	}
}
