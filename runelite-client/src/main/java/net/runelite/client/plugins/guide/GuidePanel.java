package net.runelite.client.plugins.guide;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.JLabel;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.api.hooks.Callbacks;

@Slf4j
class GuidePanel extends PluginPanel
{
    private final JTextArea notesEditor = new JTextArea();
    private final UndoManager undoRedo = new UndoManager();
    public JLabel label = new JLabel("Hello World");
    public JTextArea statArea = new JTextArea();

    void init(GuideConfig config)
    {
        // this may or may not qualify as a hack
        // but this lets the editor pane expand to fill the whole parent panel
        getParent().setLayout(new BorderLayout());
        getParent().add(this, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        notesEditor.setTabSize(2);
        notesEditor.setLineWrap(true);
        notesEditor.setWrapStyleWord(true);

        statArea.setEditable(false);
        statArea.setLineWrap(true);
        statArea.setWrapStyleWord(true);

        JPanel notesContainer = new JPanel();
        JPanel labelContainer = new JPanel();
        notesContainer.setLayout(new BorderLayout());
        labelContainer.setLayout(new BorderLayout());
        notesContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        statArea.setOpaque(false);

        // load note text
        String data = config.notesData();
        notesEditor.setText(data);

        // setting the limit to a 500 as UndoManager registers every key press,
        // which means that be default we would be able to undo only a sentence.
        // note: the default limit is 100
        undoRedo.setLimit(500);
        notesEditor.getDocument().addUndoableEditListener(e -> undoRedo.addEdit(e.getEdit()));

        notesEditor.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");
        notesEditor.getInputMap().put(KeyStroke.getKeyStroke("control Y"), "Redo");

        notesEditor.getActionMap().put("Undo", new AbstractAction("Undo")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    if (undoRedo.canUndo())
                    {
                        undoRedo.undo();
                    }
                }
                catch (CannotUndoException ex)
                {
                    log.warn("Notes Document Unable To Undo: " + ex);
                }
            }
        });

        notesEditor.getActionMap().put("Redo", new AbstractAction("Redo")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    if (undoRedo.canRedo())
                    {
                        undoRedo.redo();
                    }
                }
                catch (CannotUndoException ex)
                {
                    log.warn("Notes Document Unable To Redo: " + ex);
                }
            }
        });

        notesEditor.addFocusListener(new FocusListener()
        {
            @Override
            public void focusGained(FocusEvent e)
            {

            }

            @Override
            public void focusLost(FocusEvent e)
            {
                notesChanged(notesEditor.getDocument());
            }

            private void notesChanged(Document doc)
            {
                try
                {
                    // get document text and save to config whenever editor is changed
                    String data = doc.getText(0, doc.getLength());
                    config.notesData(data);
                }
                catch (BadLocationException ex)
                {
                    log.warn("Notes Document Bad Location: " + ex);
                }
            }
        });
        notesContainer.add(notesEditor, BorderLayout.CENTER);
        notesContainer.setBorder(new EmptyBorder(10, 10, 10, 10));

        labelContainer.add(statArea, BorderLayout.CENTER);
        labelContainer.setBorder(new EmptyBorder(10, 10, 10, 10));

        // add(notesContainer, BorderLayout.CENTER);
        add(labelContainer, BorderLayout.NORTH);
    }

    void setStats(String data)
    {
        statArea.setText(data);
    }

    void setLabel(String data) { label.setText(data);}
}
