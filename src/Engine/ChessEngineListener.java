package Engine;

import java.util.EventListener;

public interface ChessEngineListener extends EventListener {

	void analysisInfo(AnalysisInfo info);
	//void analysisReady(String bestMove);
}
