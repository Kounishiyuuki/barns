# data

Data holds repository implementations, DTOs, mappers, and local/remote data
sources. It implements the domain repository interfaces.

Planned:
- repository: mock/local implementations (e.g. MockAuthRepository,
  MockProductItemRepository, MockCareRepository, MockPatternRepository,
  MockSupportRepository, MockConsultationDraftRepository)
- dto + mapper: external representations and conversions to domain models
- datasource/local, datasource/remote: added when persistence / mock API land

Rules:
- DTOs stay inside data and must not be passed to presentation.
- Customer-side private data (registered items, care logs, consultation
  drafts, photos, contact info, personal notes) is local-first and must
  never be sent to a server.
- The remote data source may serve only categories, patterns, care guides,
  notices, and company info.
