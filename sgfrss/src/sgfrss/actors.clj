(ns sgfrss.actors)

(defn make-actor [f period-in-ms & initial-state]
  (agent (into [f period-in-ms false] initial-state)))

(defmacro actor
  "Creates and returns a new, initially-sleeping actor with the specified period, initial parameter values, and code to execute."
  [period-in-ms initial-bindings & body]
  `(let [p# ~period-in-ms]
     (make-actor
       (fn [~@(take-nth 2 initial-bindings)] ~@body)
       p#
       ~@(take-nth 2 (rest initial-bindings)))))

(defn- actor-act [state]
  (when (nth state 2)
    (apply (first state) (drop 3 state))
    (Thread/sleep (second state))
    (send-off *agent* actor-act))
  state)

(defn- actor-start [state]
  (send-off *agent* actor-act)
  (into [(first state) (second state) true] (drop 3 state)))

(defn- actor-stop [state]
  (into [(first state) (second state) false] (drop 3 state)))

(defn- actor-change-state [state new-state]
  (into [(first state) (second state) (nth state 2)] new-state))

(defn start-actor
  "Wakes up an actor -- starts it periodically executing its body."
  [actor]
  (send-off actor actor-start))

(defn stop-actor
  "Puts an actor to sleep again."
  [actor]
  (send-off actor actor-stop))

(defn change-actor-state
  "Changes an actor's parameter list."
  [actor & new-state]
  (send-off actor actor-change-state new-state))

