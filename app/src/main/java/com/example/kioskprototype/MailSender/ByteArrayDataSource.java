package com.example.kioskprototype.MailSender;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

/**
 * Class in charge of converting the String needed for the body of the mail to a byte array
 */
public class ByteArrayDataSource implements DataSource {
    private byte[] data;
    private String type;

    /**
     * ByteArrayDataSource constructor
     * @param data
     *              Body of the mail
     * @param type
     *              Type of text
     */
    ByteArrayDataSource(byte[] data, String type) {
        super();
        this.data = data;
        this.type = type;
    }

    /**
     * Second constructor of the ByteArrayDataSource
     * @param data
     *              Body of the mail
     */
    public ByteArrayDataSource(byte[] data) {
        super();
        this.data = data;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * ByteArrayDataSource type getter
     * @return
     *                  DataSource type
     */
    public String getContentType() {
        if (type == null)
            return "application/octet-stream";
        else
            return type;
    }

    /**
     * Input stream getter
     * @return
     *          input stream
     */
    public InputStream getInputStream() {
        return new ByteArrayInputStream(data);
    }

    /**
     * Get name of the object
     * @return
     *          Name
     */
    public String getName() {
        return "ByteArrayDataSource";
    }

    /**
     * Output stream getter
     * @return
     *          output stream
     * @throws IOException
     *          Exception
     */
    public OutputStream getOutputStream() throws IOException {
        throw new IOException("Not Supported");
    }
}
