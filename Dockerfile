FROM knowesis/so:base

ENV $SO_HOME /opt/knowesis/sift/orchestrator


COPY target/sfmc-smshandler-exec/bin  $SO_HOME/bin/

COPY target/sfmc-smshandler-exec/lib  $SO_HOME/lib/

COPY target/sfmc-smshandler-exec/conf $SO_HOME/conf

COPY target/sfmc-smshandler-exec/flow $SO_HOME/flow

WORKDIR $SO_HOME

RUN pwd

CMD ["bin/sfmc-smshandler.sh","start"]
