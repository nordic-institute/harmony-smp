package eu.europa.ec.cipa.as2wrapper.test;

import java.io.IOException;
import javax.xml.bind.*;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import eu.europa.ec.cipa.as2wrapper.types.RequestType;

public class SchemaGenTest {

    public static void main(String[] args) throws Exception
    {
        JAXBContext jc = JAXBContext.newInstance(RequestType.class);   
        jc.generateSchema(new SchemaOutputResolver() {

            @Override
            public Result createOutput(String namespaceURI, String suggestedFileName) throws IOException
            {
                return new StreamResult(suggestedFileName);
            }

        });

    }

}