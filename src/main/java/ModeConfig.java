import java.nio.file.Path;
import java.util.List;

/*
 * Layer: framework / configuration
 * Role: Immutable configuration of input path, output path, Mode selected by ArgsValidator, and song selected for PA5.
 * Depends on: Path, Mode
 * Used by: Cs214Project, all Runner implementations
 */
public record ModeConfig(Path input, Path output, Mode mode, List<String> songSelections, int k) {
}
