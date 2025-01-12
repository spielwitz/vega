/**	VEGA - a strategy game
    Copyright (C) 1989-2025 Michael Schweitzer, spielwitz@icloud.com

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>. **/

package resourceBundleUtility;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.formdev.flatlaf.FlatLightLaf;

import commonUi.FontHelper;
import commonUi.UiConstants;
import uiBaseControls.Button;
import uiBaseControls.Frame;
import uiBaseControls.IButtonListener;
import uiBaseControls.Label;
import uiBaseControls.LookAndFeel;
import uiBaseControls.Panel;
import uiBaseControls.TextField;

@SuppressWarnings("serial")
public class ResourceBundleUtility extends Frame // NO_UCD (unused code)
						implements IButtonListener
{
	private static int insets;
	
	transient private static final String PROPERTIES_FILE_NAME = "ResourceBundleUtilityProperties";
	transient private static final String PROPERTY_NAME_PROPERTIES_FILE = "propertiesFile";
	transient private static final String PROPERTY_NAME_OUTPUT_CLASS_NAME = "outputClassName";
	transient private static final String PROPERTY_NAME_OUTPUT_PACKAGE_NAME = "outputPackageName";
	
	transient private static final String PROPERTY_NAME_OUTPUT_PATH = "outputClassPath";
	private static final String SYMBOL_SEPARATOR = "_";
	private static final String SYMBOL_PREFIX = "£";
	private static final String SYMBOL_PARAMETER_SEPARATOR = "§";
	
	static
	{
		FontHelper.initialize(UiConstants.FONT_NAME);
		
		LookAndFeel.set(
				new FlatLightLaf(),
				FontHelper.getFont(11), 
				10);
		
		insets = 5;
	}
	public static void main(String[] args) 
	{
		new ResourceBundleUtility();
	}
	
	private static void createConvertSymbolStringMethod(StringBuilder sb)
	{
		sb.append("\tpublic static String getString(String symbolString){\n");

		sb.append("\t\tStringBuilder sb = new StringBuilder();\n");
		sb.append("\t\tint pos = 0;\n\n");
		sb.append("\t\tdo {\n");
		sb.append("\t\t\tint startPos = symbolString.indexOf(\""+SYMBOL_PREFIX+"\", pos);\n");
		sb.append("\t\t\tif (startPos < 0){\n");
		sb.append("\t\t\t\tsb.append(symbolString.substring(pos, symbolString.length()));\n");
		sb.append("\t\t\t\tbreak;}\n");

		sb.append("\t\t\tsb.append(symbolString.substring(pos, startPos));\n");
		sb.append("\t\t\tint endPos = symbolString.indexOf(\""+SYMBOL_PREFIX+"\", startPos + 1);\n");
		sb.append("\t\t\tString subString = symbolString.substring(startPos + 1, endPos);\n");
		sb.append("\t\t\tObject[] parts = subString.split(\""+SYMBOL_PARAMETER_SEPARATOR+"\");\n");
		sb.append("\t\t\tif (symbolDict.containsKey(parts[0])){\n"); 
		sb.append("\t\t\t\tif (parts.length == 1)\n");
		sb.append("\t\t\t\t\tsb.append(messages.getString(symbolDict.get(parts[0])));\n");
		sb.append("\t\t\t\telse{\n");

		sb.append("\t\t\t\t\tObject[] args = new Object[parts.length - 1];\n");
		sb.append("\t\t\t\t\tfor (int i = 1; i < parts.length; i++)\n");
		sb.append("\t\t\t\t\t\targs[i-1] = parts[i];\n");
		
		sb.append("\t\t\t\t\t\tsb.append(MessageFormat.format(messages.getString(symbolDict.get(parts[0])) ,args));\n");
		sb.append("\t\t\t}}\n");
		sb.append("\t\t\tpos = endPos + 1;\n");
		sb.append("\t\t} while (true);\n");
		sb.append("\t\treturn sb.toString();\n");
		sb.append("\t}\n");
	}
	private Properties properties;
	
	private String propertiesFile = "";
	
	private String outputPackageName = "";
	private String outputClassName = "";
	private String outputClassPath = "";
	private TextField tfPropertiesFile;
	private TextField tfOutputPackageName;
	
	private TextField tfOutputClassName;
	private TextField tfOutputClassPath;
	private Button butBrowsePropertiesFile;
	
	private Button butBrowseOutputClassPath;
	
	private Button butCreate;
	
	private Button butCancel;

	public ResourceBundleUtility()
	{
		super("Resource Bundle Utility (c) M. Schweitzer", new GridBagLayout());

		Panel panBase = new Panel(new BorderLayout(insets, insets));

		this.properties = this.getProperties();
		
		// -----
		Panel panMain = new Panel(new GridBagLayout());
		GridBagConstraints cPanMain = new GridBagConstraints();
		
		cPanMain.insets = new Insets(insets, insets, insets, insets);
		cPanMain.weightx = 0;
		cPanMain.weighty = 0.5;
		cPanMain.gridwidth = 1;
		cPanMain.fill = GridBagConstraints.HORIZONTAL;
		
		int columns = 60;
		
		cPanMain.gridx = 0;
		cPanMain.gridy = 0;
		
		panMain.add(new Label("Properties File"), cPanMain);
				
		cPanMain.gridx = 6;
		cPanMain.gridy = 0;
		this.butBrowsePropertiesFile = new Button("Browse...", this);
		panMain.add(this.butBrowsePropertiesFile, cPanMain);
		
		cPanMain.gridx = 0;
		cPanMain.gridy = 1;
		panMain.add(new Label("Package"), cPanMain);
		
		cPanMain.gridx = 0;
		cPanMain.gridy = 2;
		panMain.add(new Label("Class Name"), cPanMain);
		
		cPanMain.gridx = 0;
		cPanMain.gridy = 3;
		panMain.add(new Label("Output Directory"), cPanMain);
		
		cPanMain.gridx = 6;
		cPanMain.gridy = 3;
		this.butBrowseOutputClassPath = new Button("Browse...", this);
		panMain.add(this.butBrowseOutputClassPath, cPanMain);
		
		cPanMain.weightx = 1.0;
		cPanMain.gridwidth = 3;
		
		cPanMain.gridx = 1;
		cPanMain.gridy = 0;
		this.tfPropertiesFile = new TextField(this.propertiesFile, null, columns, -1, null);
		this.tfPropertiesFile.setColumns(100);
		panMain.add(this.tfPropertiesFile, cPanMain);

		cPanMain.gridx = 1;
		cPanMain.gridy = 1;
		this.tfOutputPackageName = new TextField(this.outputPackageName, null, columns, -1, null);
		this.tfOutputPackageName.setColumns(100);
		panMain.add(this.tfOutputPackageName, cPanMain);
		
		cPanMain.gridx = 1;
		cPanMain.gridy = 2;
		this.tfOutputClassName = new TextField(this.outputClassName, null, columns, -1, null);
		this.tfOutputClassName.setColumns(100);
		panMain.add(this.tfOutputClassName, cPanMain);
		
		cPanMain.gridx = 1;
		cPanMain.gridy = 3;
		this.tfOutputClassPath = new TextField(this.outputClassPath, null, columns, -1, null);
		this.tfOutputClassPath.setColumns(100);
		panMain.add(this.tfOutputClassPath, cPanMain);
		
		panBase.add(panMain, BorderLayout.CENTER);
		
		// -----
		Panel panButtons = new Panel(new FlowLayout(FlowLayout.RIGHT));
		
		this.butCancel = new Button("Quit", this);
		panButtons.add(this.butCancel);
		
		this.butCreate = new Button("Create", this);
		panButtons.add(this.butCreate);
		
		panBase.add(panButtons, BorderLayout.SOUTH);
		
		GridBagConstraints cBase = new GridBagConstraints();
		cBase.insets = new Insets(10, 10, 10, 10);
		cBase.gridx = 0;
		cBase.gridy = 0;
		cBase.weightx = 1;
		cBase.weighty = 1;
		cBase.fill = GridBagConstraints.BOTH;
		
		this.addToBasePanel(panBase, cBase);
		
		this.pack();
		this.setVisible(true);
	}

	@Override
	public void buttonClicked(Button source)
	{
		if (source == this.butCancel)
			this.close();
		else if (source == this.butBrowsePropertiesFile)
		{
			JFileChooser chooser = new JFileChooser();
			
			if (this.propertiesFile != null)
				chooser.setCurrentDirectory(new File(this.propertiesFile));
			
		    chooser.setDialogTitle("Properties File");
		    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
		    {
		    	this.propertiesFile = chooser.getSelectedFile().getAbsolutePath();
		    	this.tfPropertiesFile.setText(this.propertiesFile);
		    }
		}
		else if (source == this.butBrowseOutputClassPath)
		{
			JFileChooser chooser = new JFileChooser();
			
			if (this.outputClassPath != null)
				chooser.setCurrentDirectory(new File(this.outputClassPath));
		    chooser.setDialogTitle("Output Directory");
		    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		    chooser.setAcceptAllFileFilterUsed(false);

		    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
		    {
		    	this.outputClassPath = chooser.getSelectedFile().getAbsolutePath();
		    	this.tfOutputClassPath.setText(this.outputClassPath);
		    }
		}
		else if (source == this.butCreate)
			this.create();
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e)
	{
		this.close();
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	protected boolean confirmClose()
	{
		this.setProperties();
		return true;
	}
	
	private void create()
	{
		this.outputClassName = this.tfOutputClassName.getText().trim();
		this.outputPackageName = this.tfOutputPackageName.getText().trim();
		this.outputClassPath = this.tfOutputClassPath.getText().trim();
		this.propertiesFile = this.tfPropertiesFile.getText().trim();
		
		if (outputClassName.length() == 0 || outputClassPath.length() == 0 || propertiesFile.length() == 0)
		{
			JOptionPane.showMessageDialog(this,
				    "Please fill out all text fields!",
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		File f = null;
		
		try
		{
			f = new File(this.propertiesFile);
		}
		catch (Exception x)
		{
			JOptionPane.showMessageDialog(this,
				    "The property file does not exist!",
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		String propFileName = f.getName();
		
		int i = propFileName.indexOf(".properties");
		
		if (i < 0)
		{
			JOptionPane.showMessageDialog(this,
				    "The name of the property file has to match the following pattern:\n[Name]_[Language]_[Country].properties\nfor example, MyApp_de_DE.properties",
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		propFileName = propFileName.substring(0, i);
		
		List<String> list = new ArrayList<>();
		
		String[] parts = propFileName.split("_");
		
		if (parts.length != 3)
		{
			JOptionPane.showMessageDialog(this,
				    "The name of the property file has to match the following pattern:\\n[Name]_[Language]_[Country].properties\\nfor example, MyApp_de_DE.properties",
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		String resourceBundleName = parts[0];
		String defaultLanguage = parts[1];
		String defaultCountry = parts[2];

		try (Stream<String> stream = Files.lines(Paths.get(this.propertiesFile))) {

			list = stream
					.filter(line -> !line.startsWith("#") && line.length() > 0)
					.collect(Collectors.toList());

		} catch (IOException e) 
		{
			JOptionPane.showMessageDialog(this,
				    "The property file cannot be read!",
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Create java code
		Hashtable<String,String> symbolDict = new Hashtable<String,String>();
		Hashtable<String,String> textDict = new Hashtable<String,String>();
		ArrayList<String> symbolList = new ArrayList<String>();
		
		StringBuilder sb = new StringBuilder();
		
		if (this.outputPackageName.length() > 0)
			sb.append("package "+this.outputPackageName+";\n\n");
		sb.append("import java.util.Hashtable;\n");
		sb.append("import java.text.MessageFormat;\n");
		sb.append("import java.util.Locale;\n");
		sb.append("import java.util.ResourceBundle;\n\n");
		
		sb.append("/**\n");
		sb.append("   * This class was created with the Resource Bundle Utility from the resource file\n");
		sb.append("   *\n");
		sb.append("   *   "+propFileName+"\n");
		sb.append("   *\n");
		sb.append("   * The resource file is maintained with the Eclipse-Plugin ResourceBundle Editor.\n");
		sb.append("   */\n");
		sb.append("public class "+this.outputClassName+" \n{\n");
				
		sb.append("\tprivate static Hashtable<String,String> symbolDict;;\n");
		sb.append("\tprivate static String languageCode;\n");
		sb.append("\tprivate static ResourceBundle messages;\n\n");
		sb.append("\tstatic {\n");
		sb.append("\t\tsetLocale(\""+defaultLanguage+"-"+defaultCountry+"\");\n");
		sb.append("\t\tsymbolDict = new Hashtable<String,String>();\n");
		sb.append("\t\tfillSymbolDict();\n");
		sb.append("\t}\n\n");
		sb.append("\tpublic static void setLocale(String newLanguageCode){\n");
		sb.append("\t\tlanguageCode = newLanguageCode;\n");
		sb.append("\t\tString[] language = languageCode.split(\"-\");\n");
		sb.append("\t\tLocale currentLocale = new Locale(language[0], language[1]);\n");
		sb.append("\t\tmessages = ResourceBundle.getBundle(\""+resourceBundleName+"\", currentLocale);\n");
		sb.append("\t}\n\n");
		sb.append("\tpublic static String getLocale(){\n");
		sb.append("\t\treturn languageCode;\n");
		sb.append("\t}\n");
		
		for (String line: list)
		{
			int posEquals = line.indexOf('=');
			if (posEquals < 0)
				continue;
			
			String key = line.substring(0, posEquals).trim();
			String text = line.substring(posEquals + 1).trim();
						
			int symbolSep = key.indexOf(SYMBOL_SEPARATOR);
			
			if (symbolSep < 0)
			{
				JOptionPane.showMessageDialog(this,
					    "The text element " + key + " does not contain a symbolic key\n"
					    		+ "that is separated with "+SYMBOL_SEPARATOR+" from the key.",
					    "Error",
					    JOptionPane.ERROR_MESSAGE);
				
				return;
			}
			
			String symbol = key.substring(symbolSep+1, key.length()).trim();
			
			if (symbol.length() == 0)
			{
				JOptionPane.showMessageDialog(this,
						"The text element " + key + " does not contain a symbolic key\n"
					    		+ "that is separated with "+SYMBOL_SEPARATOR+" from the key.",
					    "Error",
					    JOptionPane.ERROR_MESSAGE);
				
				return;
			}
			
			if (symbolDict.containsKey(symbol))
			{
				JOptionPane.showMessageDialog(this,
					    "The text element " + key + " uses the symbolic key\n"
					    		+ symbol +" thst is already used by another text element.",
					    "Error",
					    JOptionPane.ERROR_MESSAGE);
				
				return;
			}
			
			textDict.put(key, text);
			symbolDict.put(symbol, key);
			symbolList.add(symbol);
		}
		
		Collections.sort(symbolList);
		
		sb.append("\n\tprivate static void fillSymbolDict() {\n");
		if (symbolList.size() > 0)
			sb.append("\t\t// Last used symbolic key: " + symbolList.get(symbolList.size() - 1) + "\n");
		
		for (String symbol: symbolList)
		{
			sb.append("\t\tsymbolDict.put(\""+symbol+"\",\""+symbolDict.get(symbol)+"\");\n");
		}
		sb.append("\t}\n");
		
		createConvertSymbolStringMethod(sb);
				
		for (String symbol: symbolList)
		{
			String key = symbolDict.get(symbol);
			String text = textDict.get(key);
			
			String keyShort = key.substring(0, key.indexOf(SYMBOL_SEPARATOR));
			int numArgs = this.getNumArguments(text);
			
			sb.append("\n\t/**\n");
			sb.append("\t   * "+text+" ["+symbol+"]\n");
			sb.append("\t   */\n");
			sb.append("\tpublic static String "+keyShort+this.getArgs(numArgs)+" {\n");
			sb.append("\t\treturn symbol ? "+this.getSymbolMethode(symbol, numArgs)+":"+this.getLangMethode(key, numArgs)+";\n");
			sb.append("\t}\n");			
		}
		
		sb.append("}");

		Path path = Paths.get(this.outputClassPath, this.outputClassName + ".java");
		 
		try (BufferedWriter writer = Files.newBufferedWriter(path))
		{
		    writer.write(sb.toString());
		}
		catch (Exception x)
		{
			JOptionPane.showMessageDialog(this,
				    "The file\n"+path.getFileName()+"\ncould not be written.",
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		JOptionPane.showMessageDialog(this,
			    "Success!",
			    "Success",
			    JOptionPane.INFORMATION_MESSAGE);
	}
	
	private String getArgs(int numArgs)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("(boolean symbol");
		
		for (int i = 0; i < numArgs; i++)
		{
			sb.append(", String arg"+i);
		}
		
		sb.append(")");
		
		return sb.toString();
	}
	
	private String getLangMethode(String key, int numArgs)
	{
		StringBuilder sb = new StringBuilder();
		
		if (numArgs == 0)
			sb.append("messages.getString(\""+key+"\")");
		else
		{
			sb.append("MessageFormat.format(");
			
			sb.append("messages.getString(\""+key+"\")");
			
			for (int i = 0; i < numArgs; i++)
			{
				sb.append(", arg" + i);
			}
			
			sb.append(")");
		}
			
		
		
		return sb.toString();
	}
	
	private int getNumArguments(String text)
	{
		int count = 0;
		
		do
		{
			if (text.indexOf("{"+count+"}") < 0)
				break;
			
			count++;
			
		} while (true);
		
		return count;
	}
	
	private Properties getProperties()
	{
		Reader reader = null;
		Properties prop = new Properties(); 

		try
		{
		  reader = new FileReader(PROPERTIES_FILE_NAME);

		  prop.load( reader );
		}
		catch ( Exception e )
		{
		}
		finally
		{
		  try { reader.close(); } catch ( Exception e ) { }
		}
		
		if (prop.containsKey(PROPERTY_NAME_PROPERTIES_FILE))
			this.propertiesFile = prop.getProperty(PROPERTY_NAME_PROPERTIES_FILE);
		
		if (prop.containsKey(PROPERTY_NAME_OUTPUT_PACKAGE_NAME))
			this.outputPackageName = prop.getProperty(PROPERTY_NAME_OUTPUT_PACKAGE_NAME);
		
		if (prop.containsKey(PROPERTY_NAME_OUTPUT_CLASS_NAME))
			this.outputClassName = prop.getProperty(PROPERTY_NAME_OUTPUT_CLASS_NAME);
		
		if (prop.containsKey(PROPERTY_NAME_OUTPUT_PATH))
			this.outputClassPath = prop.getProperty(PROPERTY_NAME_OUTPUT_PATH);
		
		return prop;
	}

	private String getSymbolMethode(String symbol, int numArgs)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("\"");
		sb.append(SYMBOL_PREFIX);
		sb.append(symbol);
		
		if (numArgs > 0)
		{
			for (int i = 0; i < numArgs; i++)
			{
				if (i > 0)
					sb.append("+\"");
				
				sb.append(SYMBOL_PARAMETER_SEPARATOR);
				sb.append("\"+arg"+i);
			}
			
			sb.append("+\"");
			sb.append(SYMBOL_PREFIX);
			sb.append("\"");
		}
		else
		{
			sb.append(SYMBOL_PREFIX);
			sb.append("\"");
		}
		
		
		return sb.toString();
	}

	private void setProperties()
	{
		this.properties.setProperty(PROPERTY_NAME_OUTPUT_PACKAGE_NAME, this.tfOutputPackageName.getText());
		this.properties.setProperty(PROPERTY_NAME_OUTPUT_CLASS_NAME, this.tfOutputClassName.getText());
		this.properties.setProperty(PROPERTY_NAME_OUTPUT_PATH, this.tfOutputClassPath.getText());
		this.properties.setProperty(PROPERTY_NAME_PROPERTIES_FILE, this.tfPropertiesFile.getText());
		
		Writer writer = null;

		try
		{
		  writer = new FileWriter(PROPERTIES_FILE_NAME);

		  properties.store( writer, "Resource Bunde Utlity" );
		}
		catch ( IOException e )
		{
		  e.printStackTrace();
		}
		finally
		{
		  try { writer.close(); } catch ( Exception e ) { }
		}

	}
}
