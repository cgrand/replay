# replay

Instant test suites from repl transcripts!

    [net.cgrand/replay "0.1.1"]

## This is tiring:

```clj
(comment "I. Explore in the REPL")

=> (+ 1 1)
2

(comment "II. Then re-arrange the REPL session to form a unit test")

(t/deftest addition-test
  (t/is (= (+ 1 1)
           3)))

(comment "III. Unit tests")

=> (addition-test)

FAIL in (addition-test) (NO_SOURCE_FILE:1)
expected: (= (+ 1 1) 3)
  actual: (not (= 2 3))
```

## Enter replay!

```clj
(comment "I. Explore in the REPL")

=> (+ 1 1)
2

(comment "II. Just cut & paste your curated REPL session")

(replay addition-lazy-test
  => (+ 1 1)
  3)

(comment "III. Unit tests for (almost) free!")

=> (addition-lazy-test)

FAIL in (addition-lazy-test) (NO_SOURCE_FILE:1)
expected: (clojure.core/= *1 (quote 3))
  actual: (not (clojure.core/= 2 3))
```

## Usage

Copy and paste a repl transcript inside a replay form, give it a name, et voila!

    (replay addition
    => (+ 2 2)
    4
    ^:eg => (rand-int 6)
    4
    ^:nd => (shuffle [1 1 2 3])
    [1 2 1 3]
    => 39
    => (+ *1 3)
    42)

See https://gist.github.com/cgrand/a52879f4a5edc4c4cc46 for an example.

    => (doc replay)
    -------------------------
    net.cgrand.replay/replay
    ([name options? & body])
    Macro
      Creates a test out of a curated repl session. Takes a name for the test,
       an inline set of options (as alternating keywords and values) and the repl
       transcript.
       Prompts support metadata: :eg means the result should not be tested unless
       *test-eg* is true, :nd means that output order is irrelevant (non determinist)
       Options are:
         :prompt symbol -- defaults to '=>,
         :wrap-with symbol -- defaults to `do,
         :before form -- form added at the start of the wrapped block,
         :after form -- form added at the end of the wrapped block.

## License

Copyright © 2013 Christophe Grand

Distributed under the Eclipse Public License, the same as Clojure.
