package com.lulj.base.utils.http;

import com.lulj.base.exception.CommonException;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


/**
 * @author lu
 */
public class HttpClient {

    private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);

    public static String doHttp(String url, Map<String, String> header, byte[] body) throws CommonException {
        DefaultHttpClient client = new DefaultHttpClient();
        try {
            if (StringUtils.isEmpty(url)) {
                return null;
            }
            HttpPost httpPost = new HttpPost(url);
            logger.info("HttpClient url:[{}]", url);

            if (header != null) {
                StringBuffer headerStr = new StringBuffer();
                for (Map.Entry<String, String> entry : header.entrySet()) {
                    httpPost.setHeader(entry.getKey(), entry.getValue());
                    headerStr.append("\"").append(entry.getKey()).append("\":\"").append(entry.getValue())
                            .append("\"\r\n");
                }
                logger.info("HttpClient headers:[{}]", headerStr.toString());
            }

            if (body != null) {
                HttpEntity entity = new ByteArrayEntity(body);
                httpPost.setEntity(entity);
                logger.info("HttpClient body's length:[{}]", body.length);
            }

            String resp = null;
            for (int i = 1; i < 4; i++) {
                logger.info("The {} time to send HTTP request.", i);
                HttpResponse response = client.execute(httpPost);
                if (response != null) {
                    int status = response.getStatusLine().getStatusCode();
                    if (HttpStatus.SC_OK == status) {
                        resp = StreamUtil.readContentByStream(response.getEntity().getContent());
                        logger.info("HTTP client response[{}].", resp);
                        i = 4;
                    } else {
                        logger.info("HTTP client response error. StatusCode[{}].", status);
                        Thread.sleep(3000);
                    }
                }
            }
            return resp;
        } catch (Exception e) {
            throw new CommonException("616999", e.getMessage());
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

}
