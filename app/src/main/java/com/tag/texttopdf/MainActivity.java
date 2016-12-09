package com.tag.texttopdf;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends Activity {

    private PdfWriter pdfWriter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        String FILE = Environment.getExternalStorageDirectory().toString()
                + "/PDF/" + "Name.pdf";

        // Create New Blank Document
        Document document = new Document(PageSize.A4, 0, 0, 0, 0);

        // Create Directory in External Storage
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/PDF");
        myDir.mkdirs();

        // Create Pdf Writer for Writting into New Created Document
        try {
            pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(FILE));

            // Open Document for Writting into document
            document.open();

            // User Define Method
            addMetaData(document);
            addTextData(document, "Hiren Patel");
//            absText("86/100", 60, 60);
//            addTitlePage(document);
            addImageData(document);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        // Close Document after writting all content
        document.close();

        Toast.makeText(this, "PDF File isreated. Location : " + FILE, Toast.LENGTH_LONG).show();
    }

    private void addTextData(Document document, String text) {
        PdfContentByte cb = pdfWriter.getDirectContent();
        cb.beginText();
        try {
            BaseFont f_cn = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            cb.setFontAndSize(f_cn, 40);

            float requiredX = 50;
            float requiredY = 50;
            Paint paint = new Paint();
            paint.setTextSize(40);
            Typeface typeface=Typeface.createFromAsset(getAssets(), "Helvetica.ttf");
            paint.setTypeface(typeface);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.FILL);

            Rect result = new Rect();
            paint.getTextBounds(text, 0, text.length(), result);
            Log.i("Text dimensions", "Width: "+result.width()+"-Height: "+result.height());
            float calculatedY = document.getPageSize().getHeight() - result.height() - requiredY;
            cb.setTextMatrix(requiredX, calculatedY);
            cb.showText(text);
            cb.endText();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void addImageData(Document document) {

        // 1st image

        Drawable pattern = getResources().getDrawable(R.drawable.bg);
        Bitmap patternBmp = ((BitmapDrawable) pattern).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        patternBmp.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        try {
            Image patternImage = Image.getInstance(byteArrayOutputStream.toByteArray());
            int indentation = 0;
            float scaler = ((document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin() - indentation) / patternImage.getWidth()) * 100;
            patternImage.scalePercent(scaler);
            document.add(patternImage);
        } catch (BadElementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        // 2nd image

        Drawable myDrawable = getResources().getDrawable(R.drawable.tick);
        Bitmap bmp = ((BitmapDrawable) myDrawable).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        Image signature;
        try {
            signature = Image.getInstance(stream.toByteArray());

            float requiredX = 50;
            float requiredY = 50;
            float calculatedY = document.getPageSize().getHeight() - bmp.getHeight() - requiredY;
            signature.setAbsolutePosition(requiredX, calculatedY);
//            signature.scalePercent(100f);
            document.add(signature);

        } catch (BadElementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        document.close();
    }

    // Set PDF document Properties
    public void addMetaData(Document document)

    {
        document.addTitle("RESUME");
        document.addSubject("Person Info");
        document.addKeywords("Personal,	Education, Skills");
        document.addAuthor("TAG");
        document.addCreator("TAG");
    }

    public void addTitlePage(Document document) throws DocumentException {
        // Font Style for Document
        Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
        Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 22, Font.BOLD
                | Font.UNDERLINE, BaseColor.GRAY);
        Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
        Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);

        // Start New Paragraph
        Paragraph prHead = new Paragraph();
        // Set Font in this Paragraph
        prHead.setFont(titleFont);
        // Add item into Paragraph
        prHead.add("RESUME – Name\n");

        // Create Table into Document with 1 Row
        PdfPTable myTable = new PdfPTable(1);
        // 100.0f mean width of table is same as Document size
        myTable.setWidthPercentage(100.0f);

        // Create New Cell into Table
        PdfPCell myCell = new PdfPCell(new Paragraph(""));
        myCell.setBorder(Rectangle.BOTTOM);

        // Add Cell into Table
        myTable.addCell(myCell);

        prHead.setFont(catFont);
        prHead.add("\nName1 Name2\n");
        prHead.setAlignment(Element.ALIGN_CENTER);

        // Add all above details into Document
        document.add(prHead);
        document.add(myTable);

        document.add(myTable);

        // Now Start another New Paragraph
        Paragraph prPersinalInfo = new Paragraph();
        prPersinalInfo.setFont(smallBold);
        prPersinalInfo.add("Address 1\n");
        prPersinalInfo.add("Address 2\n");
        prPersinalInfo.add("City: SanFran. State: CA\n");
        prPersinalInfo.add("Country: USA Zip Code: 000001\n");
        prPersinalInfo.add("Mobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \n");


        prPersinalInfo.setAlignment(Element.ALIGN_CENTER);

        document.add(prPersinalInfo);
        document.add(myTable);

        document.add(myTable);

        Paragraph prProfile = new Paragraph();
        prProfile.setFont(smallBold);
        prProfile.add("\n \n Profile : \n ");
        prProfile.setFont(normal);
        prProfile
                .add("\nI am Mr. XYZ. I am Android Application Developer at TAG.");

        prProfile.setFont(smallBold);
        document.add(prProfile);

        // Create new Page in PDF
        document.newPage();
    }
}
