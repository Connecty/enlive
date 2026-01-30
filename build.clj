(ns build
  (:require [clojure.tools.build.api :as b]))

(def lib 'io.github.connecty/enlive)
(def version "2.0.0")
(def class-dir "target/classes")
(def basis (b/create-basis {:project "deps.edn"}))
(def jar-file (format "target/%s-%s.jar" (name lib) version))

(defn clean [_]
  (b/delete {:path "target"}))

(defn jar [_]
  (clean nil)
  (b/write-pom {:class-dir class-dir
                :lib lib
                :version version
                :basis basis
                :src-dirs ["src"]
                :scm {:url "https://github.com/Connecty/enlive"
                      :connection "scm:git:git://github.com/Connecty/enlive.git"
                      :developerConnection "scm:git:ssh://git@github.com/Connecty/enlive.git"
                      :tag (str "v" version)}
                :pom-data [[:description "A selector-based (à la CSS) templating and transformation system for Clojure"]
                           [:url "https://github.com/Connecty/enlive"]
                           [:licenses
                            [:license
                             [:name "Eclipse Public License 1.0"]
                             [:url "http://opensource.org/licenses/eclipse-1.0.php"]]]]})
  (b/copy-dir {:src-dirs ["src"]
               :target-dir class-dir})
  (b/jar {:class-dir class-dir
          :jar-file jar-file})
  (println (str "Built: " jar-file)))

(defn install [_]
  (jar nil)
  (b/install {:basis basis
              :lib lib
              :version version
              :jar-file jar-file
              :class-dir class-dir})
  (println (str "Installed: " lib " " version)))
