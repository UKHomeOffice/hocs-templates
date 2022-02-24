FROM quay.io/ukhomeofficedigital/hocs-base-image as builder

USER root

COPY build/libs/hocs-templates.jar .
COPY scripts/run.sh .

RUN java -Djarmode=layertools -jar hocs-templates.jar extract

FROM quay.io/ukhomeofficedigital/hocs-base-image

ENV USER user_hocs
ENV USER_ID 10000
ENV GROUP group_hocs

USER root

RUN addgroup -S ${GROUP} && adduser -S -u ${USER_ID} ${USER} -G ${GROUP} -h /app

USER ${USER_ID}

WORKDIR /app

COPY --from=builder --chown=${USER}:${GROUP} run.sh ./
COPY --from=builder --chown=${USER}:${GROUP} dependencies/ ./
COPY --from=builder --chown=${USER}:${GROUP} spring-boot-loader/ ./
COPY --from=builder --chown=${USER}:${GROUP} application/ ./

CMD ["sh", "/app/run.sh"]
