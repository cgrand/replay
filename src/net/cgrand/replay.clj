;   Copyright (c) Christophe Grand, 2013. All rights reserved.

;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this 
;   distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns net.cgrand.replay
  "Instant test suites."
  (:use clojure.test))

(def ^:dynamic *test-eg* 
  "When set to true, even example outputs in replay forms are tested." false)

(defn- split-inputs [prompt forms]
  (loop [m nil inputs [] s (seq forms)] 
    (when-let [[x & xs] s]
      (if (= prompt x)
        (recur (meta x) (if xs (conj inputs (first xs)) inputs) (next xs))
        [m inputs s]))))

(defmacro do123 [& forms]
  (let [r (gensym "r")
        bindings (into '[*3 nil *2 nil *1 nil]
                   (mapcat (fn [f] [r f '*3 '*2 '*2 '*1 '*1 r])
                     forms))]
    `(let ~bindings ~'*1)))

(defn nd=  [& colls]
  (apply = (map frequencies colls)))

(defmacro replay 
  "Creates a test out of a curated repl session. Takes a name for the test,
   an inline set of options (as alternating keywords and values) and the repl
   transcript (*1, *2 and *3 are supported).
   Prompts support metadata: :eg means the result should not be tested unless
   *test-eg* is true, :nd means that output order is irrelevant (non determinist)
   Options are: 
     :prompt symbol -- defaults to '=>,
     :wrap-with symbol -- defaults to `do,
     :before form -- form added at the start of the wrapped block,
     :after form -- form added at the end of the wrapped block."
  [name options? & body]
  (let [body (cons options? body)
        {wrap :wrap-with :keys [before after prompt] :or {wrap `do prompt '=>} :as options}
          (into {} (map vec (take-while (comp keyword? first) (partition 2 body))))
        body (drop (* 2 (count options)) body)]
    `(deftest ~name 
       ~@(for [[m ins out] (->> 
                             (iterate (fn [[_ _ s]] (split-inputs prompt (next s))) 
                               [nil nil (cons nil body)])
                             next
                             (take-while (fn [[_ _ s]] (seq s)))
                             (map (fn [[m in s]] [m in (first s)])))]
           (let [eq (if (:nd m) `nd= `=)
                 is-form `(is (~eq (~wrap ~before (let [r# (do123 ~@ins)] ~after r#)) '~out))
                 is-form (with-meta is-form (meta (last ins)))]
             (if (:eg m) 
               `(when *test-eg* ~is-form)
               is-form))))))


