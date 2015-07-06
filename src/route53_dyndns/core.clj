(ns route53-dyndns.core
  (:use [amazonica.aws.route53])
  (:import [com.amazonaws.services.route53.model ResourceRecord ResourceRecordSet Change ChangeBatch]))

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


;(change-resource-record-sets 
;  :hosted-zone-id "Z1SFTI1M7NYHOB" 
;  :change-batch (create-change "test.digitaljedi.com" (external-ip)))

;To make the above work
;1) Convert hostname to domain
;2) Resolve domain zoneid

