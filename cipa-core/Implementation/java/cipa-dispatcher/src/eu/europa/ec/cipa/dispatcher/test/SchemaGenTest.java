package eu.europa.ec.cipa.dispatcher.test;

import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

public class SchemaGenTest {

    public static void main(String[] args) throws Exception
    {
        JAXBContext jc = JAXBContext.newInstance(Object.class);  //RequestType.class   
        jc.generateSchema(new SchemaOutputResolver() {

            @Override
            public Result createOutput(String namespaceURI, String suggestedFileName) throws IOException
            {
                return new StreamResult(suggestedFileName);
            }

        });

    }

}