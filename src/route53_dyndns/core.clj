(ns route53-dyndns.core
  (:use [amazonica.aws.route53])
  (:import [com.amazonaws.services.route53.model ResourceRecord ResourceRecordSet Change ChangeBatch])
  (:gen-class))

(defn list-zone-names []
  (let [zones (list-hosted-zones)]
    (map :name (:hosted-zones zones))))

(defn create-change [hostname ip]
  (let [rr (list (ResourceRecord. ip))
        rrs (ResourceRecordSet. hostname "A")
        c (Change. "UPSERT" rrs)
        cb (ChangeBatch. (list c))]
    (.setResourceRecords rrs rr)
    (.setTTL rrs 300)
    cb))

(defn external-ip []
  (clojure.string/trim (slurp "http://icanhazip.com/")))

(defn hosted-zone-name [hostname]
  (let [host (reverse (clojure.string/split hostname #"\."))]
    (str (second host) "." (first host) ".")))

(defn hosted-zone [hostname]
  (let [name (hosted-zone-name hostname)]
    (first (filter #(= (:name %) name) (:hosted-zones (list-hosted-zones))))))

(defn update-route53 [hostname]
  (let [zone (hosted-zone hostname)]
    (change-resource-record-sets 
      :hosted-zone-id (:id zone)
      :change-batch (create-change hostname (external-ip)))))

(defn -main [& args] 
  (update-route53 (first args)))

