package test;

import java.beans.PropertyVetoException;
import java.util.Locale;

import javax.speech.AudioException;
import javax.speech.Central;
import javax.speech.EngineException;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.speech.synthesis.Voice;

public class SpeechSample {
	private Synthesizer synthesizer = null;

	public SpeechSample() {
		// シンセザイザのモードを指定
		System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
		SynthesizerModeDesc desc = new SynthesizerModeDesc(null, "general", Locale.US, Boolean.FALSE, null);
        
		try {
			Central.registerEngineCentral("com.sun.speech.freetts.jsapi.FreeTTSEngineCentral");
			// シンセザイザを作成
			synthesizer = Central.createSynthesizer(desc);
			if (synthesizer == null) {
				System.err.println("ERROR! シンセザイザが見つかりません。");
				System.exit(1);
			}

			// ボイスを作成
			String voiceName = "kevin16";
			Voice voice = new Voice(voiceName, Voice.GENDER_DONT_CARE, Voice.AGE_DONT_CARE, null);
			if (voice == null){
				System.err.println("ERROR! シンセザイザがボイス " + voiceName + " をサポートしていません。");
				System.exit(1);
			}

			// リソースの割り当て
			synthesizer.allocate();
			synthesizer.resume();
			// ボイスの設定
			synthesizer.getSynthesizerProperties().setVoice(voice);
		}
		catch (EngineException ex) {
			ex.printStackTrace();
		} catch (PropertyVetoException ex) {
			ex.printStackTrace();
		} catch (AudioException ex) {
			ex.printStackTrace();
		}
	}

	public void speak(String message) {
		try {
			// テキストの読み上げ
			synthesizer.speakPlainText(message, null);
			synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}

	public void deallocateSynthesizer() {
		try {
			// リソースの開放
			synthesizer.deallocate();
		} catch (EngineException ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {
		String message = "KONNN KNEAR CHIWAR.";
		
		SpeechSample sample = new SpeechSample();
		sample.speak(message);
		sample.deallocateSynthesizer();
	}
}