# domain/usecase

Use cases hold the app's business operations. Each use case wraps a single
intent and depends only on domain models and repository interfaces.

Planned use cases mirror the iOS reference (one folder per feature):

- auth: GetCurrentUser, LoginAsGuest
- myitems: GetProductItems, GetProductItemDetail, AddProductItem
- care: GetCareTasks, GetCareTaskDetail, CompleteCareTask, GetCareLogs
- patterns: GetPatterns, GetPatternDetail
- support: GetSupportInfo, GetConsultationDraft, SaveConsultationDraft
- home: GetHomeSummary

Rules:
- Use cases must not depend on Compose, the Android framework, Room,
  Retrofit, DataStore, or any platform API.
- View models call use cases; use cases call repository interfaces.
