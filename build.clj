(ns build
  (:require [clojure.tools.build.api :as b]))

(def lib 'io.github.connecty/enlive)
(def version "2.0.1-SNAPSHOT")
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

;; CodeArtifact deploy defaults. Override per-invocation via env vars or by
;; passing args to `clj -T:build deploy-codeartifact :domain ...`.
(def codeartifact-defaults
  {:domain       "<domain>"
   :domain-owner "<account-id>"
   :region       "<region>"
   :repository   "<repository>"})

(defn- codeartifact-url [{:keys [domain domain-owner region repository]}]
  (or (System/getenv "CODEARTIFACT_REPO_URL")
      (format "https://%s-%s.d.codeartifact.%s.amazonaws.com/maven/%s/"
              (or (System/getenv "CODEARTIFACT_DOMAIN") domain)
              (or (System/getenv "CODEARTIFACT_DOMAIN_OWNER") domain-owner)
              (or (System/getenv "CODEARTIFACT_REGION") region)
              (or (System/getenv "CODEARTIFACT_REPOSITORY") repository))))

(defn deploy-codeartifact
  "Deploy the jar to AWS CodeArtifact.

  Requires the CODEARTIFACT_AUTH_TOKEN env var (obtain via
  `aws codeartifact get-authorization-token`). Repository coordinates fall
  back to the placeholders in `codeartifact-defaults`; override either via
  CODEARTIFACT_{REPO_URL,DOMAIN,DOMAIN_OWNER,REGION,REPOSITORY} env vars or
  by passing :domain/:domain-owner/:region/:repository as args."
  [opts]
  (jar nil)
  (let [token (or (System/getenv "CODEARTIFACT_AUTH_TOKEN")
                  (throw (ex-info "CODEARTIFACT_AUTH_TOKEN env var is required" {})))
        url   (codeartifact-url (merge codeartifact-defaults opts))
        deploy (requiring-resolve 'deps-deploy.deps-deploy/deploy)]
    (deploy {:installer  :remote
             :artifact   jar-file
             :pom-file   (b/pom-path {:lib lib :class-dir class-dir})
             :repository {"codeartifact" {:url      url
                                          :username "aws"
                                          :password token}}})
    (println (str "Deployed to CodeArtifact: " lib " " version " -> " url))))
