FROM quay.io/ukhomeofficedigital/hocs-base-image-build as builder

COPY . .
RUN ./gradlew clean assemble --no-daemon && java -Djarmode=layertools -jar ./build/libs/hocs-templates.jar extract

RUN jdeps --ignore-missing-deps -q --print-module-deps ./build/libs/hocs-templates.jar > jre-deps.info

RUN jlink --verbose --compress 2 --strip-java-debug-attributes --no-header-files --no-man-pages --output jre --add-modules $(cat jre-deps.info)

FROM alpine as production

WORKDIR /app

COPY --from=builder --chown=user_hocs:group_hocs ./jre ./
COPY --from=builder --chown=user_hocs:group_hocs ./scripts/run.sh ./
COPY --from=builder --chown=user_hocs:group_hocs ./spring-boot-loader/ ./
COPY --from=builder --chown=user_hocs:group_hocs ./dependencies/ ./
COPY --from=builder --chown=user_hocs:group_hocs ./application/ ./

USER 10000

CMD ["sh", "/app/run.sh"]
