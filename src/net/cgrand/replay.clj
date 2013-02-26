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
        body (drop (* 2 (count options)) body)
        r (gensym "r")]
    `(deftest ~name
       (~wrap
         ~before
         (let [~@(mapcat (fn [[ppx px x]] 
                           (when (not= x prompt)
                             (assert (or (= prompt px) (= prompt ppx)))
                             (if (= prompt px) 
                               [r x '*3 '*2 '*2 '*1 '*1 r]
                               (let [m (meta ppx)
                                     eq (if (:nd m) `nd= `=)
                                     is-form `(is (~eq ~'*1 '~x))
                                     is-form (with-meta is-form (meta px))
                                     is-form (if (:eg m) 
                                               `(when *test-eg* ~is-form)
                                               is-form)]
                                 [r is-form])))) 
                   (partition 3 1 (cons nil body)))]
           nil)
         ~after))))


