/* 
 * jBrowserDriver (TM)
 * Copyright (C) 2014-2016 jBrowserDriver committers
 * https://github.com/MachinePublishers/jBrowserDriver
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.machinepublishers.jbrowserdriver;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

class StreamHandler implements URLStreamHandlerFactory {

  private static String PREFIX = "sun.net.www.protocol";

  StreamHandler() {}

  private static class HttpHandler extends java.net.URLStreamHandler {
    @Override
    protected URLConnection openConnection(URL url) throws IOException {
      return new StreamConnection(url);
    }
  }

  static URLConnection defaultConnection(URL url) throws IOException {
    try {
      String name = "sun.net.www.protocol.http.HttpURLConnection";
      if ("https".equals(url.getProtocol())) {
        name = "sun.net.www.protocol.https.HttpsURLConnectionImpl";
      }
      Object o = Class.forName(name).getConstructor(URL.class).newInstance(url);
      return (URLConnection)o;
    } catch (ReflectiveOperationException x) {
      // ignore
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public URLStreamHandler createURLStreamHandler(String protocol) {
    if ("http".equals(protocol) || "https".equals(protocol)) {
      return new HttpHandler();
    }
    String name = PREFIX + "." + protocol + ".Handler";
    try {
      @SuppressWarnings("deprecation")
      Object o = Class.forName(name).newInstance();
      return (URLStreamHandler)o;
    } catch (ClassNotFoundException x) {
      // ignore
    } catch (Exception e) {
      // For compatibility, all Exceptions are ignored.
      // any number of exceptions can get thrown here
    }
    return null;
  }

} 