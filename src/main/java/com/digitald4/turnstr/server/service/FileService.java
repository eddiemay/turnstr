package com.digitald4.turnstr.server.service;

import com.digitald4.common.exception.DD4StorageException;
import com.digitald4.common.model.UpdateRequest;
import com.digitald4.common.proto.DD4UIProtos;
import com.digitald4.common.server.service.EntityService;
import com.digitald4.common.storage.QueryResult;
import com.digitald4.common.storage.Store;
import com.digitald4.turnstr.model.ContentItem;
import com.digitald4.turnstr.model.TurnstrUser;
import com.digitald4.turnstr.proto.Turnstr.File;
import com.digitald4.turnstr.storage.ContentItemStore;
import com.google.api.server.spi.config.Named;
import com.google.protobuf.Empty;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FileService implements EntityService<File> {

  private final Store<File> fileStore;
  private final ContentItemStore contentItemStore;
  private final Provider<HttpServletResponse> responseProvider;
  private final Provider<TurnstrUser> userProvider;

  @Inject
  public FileService(
      Store<File> fileStore,
      ContentItemStore contentItemStore,
      Provider<HttpServletResponse> responseProvider,
      Provider<TurnstrUser> userProvider) {
    this.fileStore = fileStore;
    this.contentItemStore = contentItemStore;
    this.responseProvider = responseProvider;
    this.userProvider = userProvider;
  }

  @Override
  public File create(File file) {
    ContentItem contentItem = contentItemStore.getWithAccessCheck(file.getContentType(), file.getContentId());
    contentItem.checkWriteAccess(userProvider.get());
    return fileStore.create(file);
  }

  @Override
  public File get(@Named("id") long id) {
    File file = fileStore.get(id);
    if (file == null) {
      throw new DD4StorageException("Access Denied", HttpServletResponse.SC_FORBIDDEN);
    }
    contentItemStore.getWithAccessCheck(file.getContentType(), file.getContentId());
    HttpServletResponse response = responseProvider.get();
    byte[] bytes = file.getData().toByteArray();
    response.setContentType("image/" + (!file.getFileType().isEmpty() ? file.getFileType() : "png"));
    response.setHeader("Cache-Control", "no-cache, must-revalidate");
    response.setContentLength(bytes.length);
    try {
      response.getOutputStream().write(bytes);
    } catch (IOException ioe) {
      throw new DD4StorageException("Error fetching file", ioe);
    }
    return null;
  }

  @Override
  public QueryResult<File> list(DD4UIProtos.ListRequest request) {
    throw new DD4StorageException("Unimplemented", HttpServletResponse.SC_BAD_REQUEST);
  }

  @Override
  public File update(@Named("id") long id, UpdateRequest<File> request) {
    throw new DD4StorageException("Unimplemented", HttpServletResponse.SC_BAD_REQUEST);
  }

  @Override
  public Empty delete(@Named("id") long id) {
    fileStore.delete(id);
    return Empty.getDefaultInstance();
  }

  @Override
  public DD4UIProtos.BatchDeleteResponse batchDelete(DD4UIProtos.BatchDeleteRequest request) {
    throw new DD4StorageException("Unimplemented", HttpServletResponse.SC_BAD_REQUEST);
  }
}
