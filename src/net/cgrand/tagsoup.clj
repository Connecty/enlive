;   Copyright (c) Christophe Grand, 2009-2013. All rights reserved.

;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this 
;   distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns net.cgrand.tagsoup
  (:require [net.cgrand.xml :as xml])
  (:import [java.util MissingResourceException]))

(defn- startparse-tagsoup [^org.xml.sax.InputSource s ch]
  (doto (org.ccil.cowan.tagsoup.Parser.)
    (.setFeature "http://www.ccil.org/~cowan/tagsoup/features/default-attributes" false)
    (.setFeature "http://www.ccil.org/~cowan/tagsoup/features/cdata-elements" true)
    (.setFeature "http://www.ccil.org/~cowan/tagsoup/features/ignorable-whitespace" true)
    (.setContentHandler ch)
    (.setProperty "http://www.ccil.org/~cowan/tagsoup/properties/auto-detector"
      (proxy [org.ccil.cowan.tagsoup.AutoDetector] []
        (autoDetectingReader [^java.io.InputStream is]
          (java.io.InputStreamReader. is "UTF-8"))))
    (.setProperty "http://xml.org/sax/properties/lexical-handler" ch)
    (.parse s)))

(defn- make-input-source
  "Create an InputSource from a stream or reader."
  [source]
  (cond
    (instance? java.io.InputStream source)
    (org.xml.sax.InputSource. ^java.io.InputStream source)
    (instance? java.io.Reader source)
    (org.xml.sax.InputSource. ^java.io.Reader source)
    :else
    (throw (IllegalArgumentException. 
             (str "Expected InputStream or Reader, got: " (type source))))))

(defn parser 
 "Loads and parse an HTML resource and closes the stream."
 [stream]
  (when-not stream 
    (throw (MissingResourceException. 
             "HTML resource not found. Note: resource paths are resolved from the classpath, not the filesystem. Use (clojure.java.io/resource \"path\") to verify your resource exists."
             "net.cgrand.tagsoup"
             "")))
  (filter map?
    (with-open [^java.io.Closeable stream stream]
      (xml/parse (make-input-source stream) startparse-tagsoup))))

