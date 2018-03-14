package com.hubspot.algebra;

public interface HttpError {
  default int getStatusCode() {
    return 400;
  }
}
