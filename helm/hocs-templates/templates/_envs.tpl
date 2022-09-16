{{- define "deployment.envs" }}
- name: JAVA_OPTS
  value: {{ tpl .Values.app.javaOpts . }}
- name: SERVER_PORT
  value: {{ include "hocs-app.port" . }}
- name: HOCS_CASE_SERVICE
  value: {{ tpl .Values.app.caseService . }}
- name: HOCS_INFO_SERVICE
  value: {{ tpl .Values.app.infoService . }}
- name: HOCS_DOCUMENT_SERVICE
  value: {{ tpl .Values.app.docsService . }}
- name: HOCS_BASICAUTH
  valueFrom:
    secretKeyRef:
      name: ui-casework-creds
      key: plaintext
{{- end -}}
