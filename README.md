# replay

Instant test suites from repl transcripts!

    [net.cgrand/replay "0.1.0]

## Usage

Copy and paste a repl transcript inside a replay form, give it a name, et voila!

See https://gist.github.com/cgrand/a52879f4a5edc4c4cc46 for an example.

    => (doc replay)
    -------------------------
    net.cgrand.replay/replay
    ([name options? & body])
    Macro
      Creates a test out of a curated repl session. Takes a name for the test,
       an inline set of options (as alyernating keywords and values) and the repl
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
