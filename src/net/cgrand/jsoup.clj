;   Copyright (c) Christophe Grand, 2009. All rights reserved.
;   Copyright (c) Baishampayan Ghose, 2013. All rights reserved.

;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this
;   distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns net.cgrand.jsoup
  "JSoup based parser backend."
  (:import [org.jsoup Jsoup]
           [org.jsoup.nodes Attribute Attributes Comment DataNode Document
            DocumentType Element Node TextNode XmlDeclaration]
           [org.jsoup.parser Parser Tag]))

(def ^:private ->key (comp keyword #(.toLowerCase ^String (.toString ^Object %))))

(defprotocol IEnlive
  (->nodes [d] "Convert object into Enlive node(s)."))

(extend-protocol IEnlive
  Attribute
  (->nodes [a] [(->key (.getKey a)) (.getValue a)])

  Attributes
  (->nodes [as] (not-empty (into {} (map ->nodes as))))

  Comment
  (->nodes [c] {:type :comment :data (.getData c)})

  DataNode
  (->nodes [dn] (str dn))

  Document
  (->nodes [d] (not-empty (map ->nodes (.childNodes d))))

  DocumentType
  (->nodes [dtd] {:type :dtd :data ((juxt :name :publicid :systemid) (->nodes (.attributes dtd)))})

  Element
  (->nodes [e] {:tag (->key (.tagName e))
                :attrs (->nodes (.attributes e))
                :content (not-empty (map ->nodes (.childNodes e)))})

  TextNode
  (->nodes [tn] (.getWholeText tn))

  nil
  (->nodes [_] nil))


(defn parser
  "Parse a HTML document stream into Enlive nodes using JSoup.
   Supports InputStream and Reader."
  [stream]
  (with-open [^java.io.Closeable stream stream]
    (cond
      (instance? java.io.InputStream stream)
      (->nodes (Jsoup/parse ^java.io.InputStream stream "UTF-8" ""))
      (instance? java.io.Reader stream)
      (let [content (slurp stream)]
        (->nodes (Jsoup/parse ^String content "")))
      :else
      (throw (IllegalArgumentException.
               (str "Expected InputStream or Reader, got: " (type stream)))))))
