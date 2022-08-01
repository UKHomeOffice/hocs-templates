FROM quay.io/ukhomeofficedigital/hocs-base-image-build as builder

COPY . .

RUN ./gradlew clean assemble --no-daemon && java -Djarmode=layertools -jar ./build/libs/hocs-templates.jar extract

FROM gcr.io/distroless/java17-debian11

USER nonroot:nonroot

COPY --from=builder --chown=nonroot:nonroot ./build/libs/hocs-templates.jar ./

USER 10000

ENTRYPOINT ["java","-jar","/hocs-templates.jar"]
