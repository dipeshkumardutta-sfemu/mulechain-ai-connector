package org.mule.extension.mulechain.internal.embedding.models;


/**
 * This class represents an extension connection just as example (there is no real connection with anything here c:).
 */
public final class LangchainEmbeddingModelConnection {

  private final String id;

  public LangchainEmbeddingModelConnection(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public void invalidate() {
    // do something to invalidate this connection!
  }
}
