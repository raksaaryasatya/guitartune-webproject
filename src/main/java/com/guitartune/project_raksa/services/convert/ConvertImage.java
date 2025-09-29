package com.guitartune.project_raksa.services.convert;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;

public interface ConvertImage {
    public String convertImage(Blob image) throws IOException, SQLException;
}

