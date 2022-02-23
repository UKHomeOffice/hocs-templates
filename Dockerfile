FROM quay.io/ukhomeofficedigital/hocs-base-image as builder

COPY build/libs/hocs-templates.jar .

RUN java -Djarmode=layertools -jar hocs-templates.jar extract

FROM quay.io/ukhomeofficedigital/hocs-base-image

COPY scripts/run.sh /app/scripts/run.sh

WORKDIR /app

COPY --from=builder dependencies/ ./
COPY --from=builder spring-boot-loader/ ./
COPY --from=builder application/ ./

USER 1001

CMD ["sh", "/app/scripts/run.sh"]
