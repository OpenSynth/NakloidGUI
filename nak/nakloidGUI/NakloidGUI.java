package nak.nakloidGUI;
import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.stream.Stream;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.widgets.Display;

import nak.nakloidGUI.gui.MainWindow;

public class NakloidGUI {
	public static PreferenceStore preferenceStore;

	public static void main(String[] args) throws IOException {
		if (Files.notExists(Paths.get("Nakloid.exe"))) {
			JOptionPane.showMessageDialog(new JPanel(), "\"Nakloid.exe\"が見つかりません。", "NakloidGUI", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		File lockFile = new File("lock");
		try (FileChannel fc=FileChannel.open(lockFile.toPath(),StandardOpenOption.WRITE,StandardOpenOption.CREATE); FileLock lock=fc.tryLock()) {
			if (lock == null) {
				JOptionPane.showMessageDialog(new JPanel(), "NakloidGUIは二重起動をサポートしていません。", "NakloidGUI", JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}
			File tmpdir = new File("temporary");
			try (Stream<Path> pathStream = Files.walk(tmpdir.toPath())) {
				pathStream.forEach(p->{try{Files.delete(p);}catch(Exception e){}});
			} catch (IOException e) {}
			if (!tmpdir.mkdir() && !tmpdir.isDirectory()) {
				JOptionPane.showMessageDialog(new JPanel(), "temporaryフォルダを作成できませんでした。", "NakloidGUI", JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}
			initializePreferenceValue();
			MainWindow mainWindow = new MainWindow();
			mainWindow.setBlockOnOpen(true);
			mainWindow.open();
			preferenceStore.save();
			Display.getCurrent().dispose();
		} catch (UnsatisfiedLinkError e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(new JPanel(), "ライブラリが読み込めません。\nJava Runtime Environment のバージョン及び下記エラーを確認して下さい。\n"+e.toString(), "NakloidGUI", JOptionPane.ERROR_MESSAGE);
		} catch (Exception | Error e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(new JPanel(), "原因不明なエラーが発生しました。\n"+e.toString(), "NakloidGUI", JOptionPane.ERROR_MESSAGE);
		} finally {
			Files.deleteIfExists(Paths.get("lock"));
		}
	}

	public static void initializePreferenceValue() {
		preferenceStore = new PreferenceStore("nakloidGUI.properties");
		preferenceStore.setDefault("workspace.path_nar", "");
		preferenceStore.setDefault("workspace.path_speech_ini", "./temporary/speech.ini");
		preferenceStore.setDefault("ini.input.path_input_score", "./temporary/score.nak");
		preferenceStore.setDefault("ini.input.score_mode", "nak");
		preferenceStore.setDefault("ini.input.track", 1);
		preferenceStore.setDefault("ini.input.path_lyrics", "./temporary/lyrics.txt");
		preferenceStore.setDefault("ini.input.path_input_pitches", "./temporary/pitches.pit");
		preferenceStore.setDefault("ini.input.pitches_mode", "pit");
		preferenceStore.setDefault("ini.input.pitch_frame_length", 5);
		preferenceStore.setDefault("ini.input.path_singer", "./vocal/");
		preferenceStore.setDefault("ini.input.path_prefix_map", "/temporary/prefix.map");
		preferenceStore.setDefault("ini.output.path_song", "./temporary/song.wav");
		preferenceStore.setDefault("ini.output.path_output_score", "./temporary/score.nak");
		preferenceStore.setDefault("ini.output.path_output_pitches", "./temporary/pitches.pit");
		preferenceStore.setDefault("ini.output.max_volume", 0.9);
		preferenceStore.setDefault("ini.output.compressor", false);
		preferenceStore.setDefault("ini.output.compressor_threshold", -20.0);
		preferenceStore.setDefault("ini.output.compressor_ratio", 2.5);
		preferenceStore.setDefault("ini.output.print_debug", false);
		preferenceStore.setDefault("ini.output.ms_output", 0);
		preferenceStore.setDefault("ini.vocal_library.use_pmp_cache", true);
		preferenceStore.setDefault("ini.vocal_library.use_uwc_cache", false);
		preferenceStore.setDefault("ini.note.ms_front_padding", 5);
		preferenceStore.setDefault("ini.note.ms_back_padding", 35);
		preferenceStore.setDefault("ini.arrange.auto_vowel_combining", true);
		preferenceStore.setDefault("ini.arrange.vowel_combining_volume", 0.8);
		preferenceStore.setDefault("ini.arrange.vibrato", true);
		preferenceStore.setDefault("ini.arrange.ms_vibrato_offset", 200);
		preferenceStore.setDefault("ini.arrange.ms_vibrato_width", 150);
		preferenceStore.setDefault("ini.arrange.pitch_vibrato", 16.0);
		preferenceStore.setDefault("ini.arrange.overshoot", true);
		preferenceStore.setDefault("ini.arrange.ms_overshoot", 20);
		preferenceStore.setDefault("ini.arrange.pitch_overshoot", 30.0);
		preferenceStore.setDefault("ini.arrange.preparation", true);
		preferenceStore.setDefault("ini.arrange.ms_preparation", 20);
		preferenceStore.setDefault("ini.arrange.pitch_preparation", 30.0);
		preferenceStore.setDefault("ini.arrange.pitch_vibrato", 10.0);
		preferenceStore.setDefault("ini.arrange.finefluctuation", false);
		preferenceStore.setDefault("ini.arrange.finefluctuation_deviation", 0.5);
		preferenceStore.setDefault("ini.unit_waveform_container.target_rms", 0.05);
		preferenceStore.setDefault("ini.unit_waveform_container.num_lobes", 1);
		preferenceStore.setDefault("ini.unit_waveform_container.uwc_normalize", true);
		preferenceStore.setDefault("ini.pitchmark.default_pitch", 260);
		preferenceStore.setDefault("ini.pitchmark.pitch_margin", 20);
		preferenceStore.setDefault("ini.pitchmark.xcorr_threshold", 0.00);
		preferenceStore.setDefault("ini.overlap.stretch_self_fade", true);
		preferenceStore.setDefault("ini.overlap.ms_self_fade", 400);
		preferenceStore.setDefault("ini.overlap.interpolation", false);
		preferenceStore.setDefault("ini.overlap.overlap_normalize", true);
		preferenceStore.setDefault("ini.overlap.window_modification", true);
		preferenceStore.setDefault("gui.mainWindow.vocalInfoDisplayMode", "tooltip");
		preferenceStore.setDefault("gui.mainWindow.headerHeight", 100);
		preferenceStore.setDefault("gui.mainWindow.keyboardWidth", 106);
		preferenceStore.setDefault("gui.mainWindow.numMidiNoteUpperLimit", 90);
		preferenceStore.setDefault("gui.mainWindow.numMidiNoteLowerLimit", 41);
		preferenceStore.setDefault("gui.mainWindow.baseNoteHeight", 20);
		preferenceStore.setDefault("gui.mainWindow.noteHeightUpperLimit", 100);
		preferenceStore.setDefault("gui.mainWindow.noteHeightLowerLimit", 20);
		preferenceStore.setDefault("gui.mainWindow.baseMsByPixel", 2.0);
		preferenceStore.setDefault("gui.mainWindow.msByPixelUpperLimit", 10.0);
		preferenceStore.setDefault("gui.mainWindow.msByPixelLowerLimit", 0.1);
		preferenceStore.setDefault("gui.mainWindow.displayLogWindow", true);
		preferenceStore.setDefault("gui.mainWindow.logWindowPositionX", 0);
		preferenceStore.setDefault("gui.mainWindow.logWindowPositionY", 0);
		preferenceStore.setDefault("gui.mainWindow.logWindowSizeX", 400);
		preferenceStore.setDefault("gui.mainWindow.logWindowSizeY", 300);
		preferenceStore.setDefault("gui.mainWindow.displayNotesWindow", true);
		preferenceStore.setDefault("gui.mainWindow.notesWindowPositionX", 480);
		preferenceStore.setDefault("gui.mainWindow.notesWindowPositionY", 360);
		preferenceStore.setDefault("gui.mainWindow.notesWindowSizeX", 0);
		preferenceStore.setDefault("gui.mainWindow.notesWindowSizeY", 0);
		preferenceStore.setDefault("gui.noteOption.volumeViewHeight", 150);
		preferenceStore.setDefault("gui.voiceOption.waveformGraphHeight", 150);
		try {
			preferenceStore.load();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(new JPanel(), "NakloidGUIの設定が読み込めませんでした。\n初期値に戻します。\n"+e.toString(), "NakloidGUI", JOptionPane.ERROR_MESSAGE);
		}
	}
}
