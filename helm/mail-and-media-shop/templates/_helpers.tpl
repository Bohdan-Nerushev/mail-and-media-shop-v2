{{/*
Expand the name of the chart.
*/}}
{{- define "mail-and-media-shop.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
*/}}
{{- define "mail-and-media-shop.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "mail-and-media-shop.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "mail-and-media-shop.labels" -}}
helm.sh/chart: {{ include "mail-and-media-shop.chart" . }}
{{ include "mail-and-media-shop.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "mail-and-media-shop.selectorLabels" -}}
app.kubernetes.io/name: {{ include "mail-and-media-shop.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{/*
Shop DB service name — used internally for JDBC URL
*/}}
{{- define "mail-and-media-shop.shopdb.svcName" -}}
{{- printf "%s-shop-db" (include "mail-and-media-shop.fullname" .) }}
{{- end }}

{{/*
Keycloak DB service name — used internally for Keycloak KC_DB_URL
*/}}
{{- define "mail-and-media-shop.keycloakdb.svcName" -}}
{{- printf "%s-keycloak-db" (include "mail-and-media-shop.fullname" .) }}
{{- end }}

{{/*
Redis service name — used for SPRING_DATA_REDIS_HOST
*/}}
{{- define "mail-and-media-shop.redis.svcName" -}}
{{- printf "%s-redis" (include "mail-and-media-shop.fullname" .) }}
{{- end }}

{{/*
Keycloak service name — used for Spring Security OIDC issuer URI
*/}}
{{- define "mail-and-media-shop.keycloak.svcName" -}}
{{- printf "%s-keycloak" (include "mail-and-media-shop.fullname" .) }}
{{- end }}
