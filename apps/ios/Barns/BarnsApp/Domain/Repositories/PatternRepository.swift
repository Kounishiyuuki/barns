protocol PatternRepository {
    func wallGreenPatterns() async throws -> [WallGreenPattern]
    func wallGreenPattern(id: WallGreenPattern.ID) async throws -> WallGreenPattern?
}
