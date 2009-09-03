package com.versionone.taskview.views.editors;

import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import com.versionone.taskview.views.htmleditor.HTMLEditor;

public class RichTextEditor extends DialogCellEditor {

    private final String richText;
    
    public RichTextEditor(Composite parent, String richText) {
        super(parent, SWT.NONE);
        this.richText = richText;
    }
    
    
    @Override
    protected Object openDialogBox(Control cellEditorWindow) {        
        HTMLEditor dialog = new HTMLEditor(cellEditorWindow.getShell(), richText);
        int x = dialog.open();
        if (x == Window.OK){
            return dialog.getValue();
        }
        
        return getValue();
    }

}
