package com.opinta.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by dponomarenko on 20.03.2017.
 */

@Data
@NoArgsConstructor
public class PDFForm {

    private String name;
    private byte[] pdfFile;

    public PDFForm(String name, byte[] pdfFile) {
        this.name = name;
        this.pdfFile = pdfFile;
    }
}
