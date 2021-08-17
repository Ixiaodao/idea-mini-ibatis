package net.ishchenko.idea.minibatis.format;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.Project;
import net.ishchenko.idea.minibatis.util.Icons;
import net.ishchenko.idea.minibatis.util.RestoreIbatisSqlUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.StringSelection;
import java.util.Objects;

/**
 * 控制台 右键 启动Sql格式化输出窗口
 *
 * @author lk
 * @version 1.0
 * @date 2020/8/23 17:14
 */
public class RestoreSqlForSelection extends AnAction {


    public RestoreSqlForSelection() {
        super(Icons.MyBatisIcon);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        final Project project = e.getProject();
        if (project == null) {
            return;
        }
        try {
            CaretModel caretModel = Objects.requireNonNull(e.getData(LangDataKeys.EDITOR)).getCaretModel();
            Caret currentCaret = caretModel.getCurrentCaret();
            String selectedText = currentCaret.getSelectedText();
            if (StringUtils.isNotEmpty(selectedText)) {
                //分割每一行
                String[] selectedRowText = selectedText.split("\n");
                String s = RestoreIbatisSqlUtil.restoreSql(selectedRowText[0], selectedRowText[1]);
                CopyPasteManager.getInstance().setContents(new StringSelection(s));
            }
        } catch (Exception exception) {
            CopyPasteManager.getInstance().setContents(new StringSelection("格式化SQL失败"));
        }

    }


    @Override
    public void update(@NotNull AnActionEvent event) {
        this.getTemplatePresentation().setEnabled(true);
    }
}