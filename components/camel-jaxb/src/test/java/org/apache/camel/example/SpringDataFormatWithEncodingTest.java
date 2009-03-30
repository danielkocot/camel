/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.example;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spring.SpringTestSupport;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @version $Revision$
 */
public class SpringDataFormatWithEncodingTest extends SpringTestSupport {

    public void testMarshalWithEncoding() throws Exception {
        PurchaseOrder bean = new PurchaseOrder();
        bean.setName("Beer");
        bean.setAmount(23);
        bean.setPrice(2.5);

        MockEndpoint mock = resolveMandatoryEndpoint("mock:result", MockEndpoint.class);
        mock.expectedBodiesReceived("<?xml version=\"1.0\" encoding=\"iso-8859-1\" standalone=\"yes\"?>"
                + "<purchaseOrder amount=\"23.0\" price=\"2.5\" name=\"Beer\"/>");

        template.sendBody("direct:start", bean);

        mock.assertIsSatisfied();
    }

    public void testMarshalWithEncodingPropertyInExchange() throws Exception {
        final PurchaseOrder bean = new PurchaseOrder();
        bean.setName("Beer");
        bean.setAmount(23);
        bean.setPrice(2.5);

        MockEndpoint mock = resolveMandatoryEndpoint("mock:result", MockEndpoint.class);
        mock.expectedBodiesReceived("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?>"
                + "<purchaseOrder amount=\"23.0\" price=\"2.5\" name=\"Beer\"/>");

        template.send("direct:start", new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody(bean);
                // the property should override the jaxb configuration
                exchange.setProperty(Exchange.CHARSET_NAME, "utf-8");
            }
        });

        mock.assertIsSatisfied();
    }

    protected ClassPathXmlApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("org/apache/camel/example/springDataFormatWithEncoding.xml");
    }
}