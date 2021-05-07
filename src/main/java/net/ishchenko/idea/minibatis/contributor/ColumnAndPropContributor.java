package net.ishchenko.idea.minibatis.contributor;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.completion.JavaLookupElementBuilder;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.util.xml.DomUtil;
import net.ishchenko.idea.minibatis.model.sqlmap.Delete;
import net.ishchenko.idea.minibatis.model.sqlmap.GroupTwo;
import net.ishchenko.idea.minibatis.model.sqlmap.IdDomElement;
import net.ishchenko.idea.minibatis.model.sqlmap.Insert;
import net.ishchenko.idea.minibatis.model.sqlmap.ResultMap;
import net.ishchenko.idea.minibatis.model.sqlmap.Select;
import net.ishchenko.idea.minibatis.model.sqlmap.SqlMap;
import net.ishchenko.idea.minibatis.model.sqlmap.Update;
import net.ishchenko.idea.minibatis.util.DomUtils;
import net.ishchenko.idea.minibatis.util.JavaUtils;
import net.ishchenko.idea.minibatis.util.SqlMapperUtils;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author jiwenbiao
 * @since : 2021/4/27 18:27
 */
public class ColumnAndPropContributor extends CompletionContributor {
    private static final Logger log = LoggerFactory.getLogger(ColumnAndPropContributor.class);
    public ColumnAndPropContributor() {
        System.out.println("123");
    }

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet returnResult) {
        if (parameters.getCompletionType() != CompletionType.BASIC) {
            return;
        }
        PsiElement position = parameters.getOriginalPosition();
        if (position == null) {
            return;
        }
        Editor editor = parameters.getEditor();
        Project project = editor.getProject();
        if (project == null) {
            return;
        }
        InjectedLanguageManager injectedLanguageManager = InjectedLanguageManager.getInstance(project);
        PsiFile topLevelFile = injectedLanguageManager.getTopLevelFile(position);
        int offset = parameters.getOffset();
        int domOffset = injectedLanguageManager.injectedToHost(position, position.getTextOffset());
        IdDomElement idDomElement = SqlMapperUtils.findParentIdDomElement(topLevelFile.findElementAt(domOffset));
        if (idDomElement instanceof Select) {
            Select select = (Select) idDomElement;
            // 补充入参提示
            PsiClass paramClass = select.getParameterClass().getValue();
            addLookupElement(returnResult, paramClass);
            // 补充出参提示
            PsiClass resultClass = select.getResultClass().getValue();
            addLookupElement(returnResult, resultClass);
        }
        if (idDomElement instanceof Insert || idDomElement instanceof Update || idDomElement instanceof Delete) {
            GroupTwo groupTwo = (GroupTwo) idDomElement;
            PsiClass paramClass = groupTwo.getParameterClass().getValue();
            if (paramClass == null) {
                return;
            }
            addLookupElement(returnResult, paramClass);
        }
    }

    private void addLookupElement(@NotNull CompletionResultSet returnResult, PsiClass psiClass) {
        if (psiClass != null) {
            PsiField[] settablePsiFields = JavaUtils.findSettablePsiFields(psiClass);
            for (PsiField settablePsiField : settablePsiFields) {
                LookupElementBuilder lookupElementBuilder = JavaLookupElementBuilder.forField(settablePsiField);
                lookupElementBuilder = lookupElementBuilder.withTailText("(" + psiClass.getName() + ")");
                lookupElementBuilder = lookupElementBuilder.withTypeText(psiClass.getName());
                returnResult.addElement(lookupElementBuilder);
            }
        }
    }

    private boolean isResult(PsiFile file, int offset) {

        String text = file.getText();
        for (int i = offset - 1; i > 0; i--) {
            char c = text.charAt(i);
            if (c == '#') {
                break;
            }
        }
        return false;
    }

    private boolean isAs(PsiFile file, int offset) {
        String text = file.getText();
        for (int i = offset - 1; i > 0; i--) {
            char c = text.charAt(i);
            String str = String.valueOf(c);
            if ("\n".equals(str)) {
                break;
            }
            if ("s".equalsIgnoreCase(str) && "a".equalsIgnoreCase(String.valueOf(text.charAt(i-1))) && " ".equals(String.valueOf(text.charAt(i-2)))) {
                return true;
            }
        }
        return false;
    }


    @Nullable
    private List<ResultMap> getResultMaps(@NotNull CompletionParameters parameters) {
        if (parameters.getCompletionType() != CompletionType.BASIC) {
            return null;
        }
        PsiElement position = parameters.getOriginalPosition();
        if (position == null) {
            return null;
        }
        Editor editor = parameters.getEditor();
        Project project = editor.getProject();
        if (project == null) {
            return null;
        }
        SqlMap mapper = getMapper(position, project);
        if (mapper == null) {
            return null;
        }
        List<ResultMap> resultMaps = mapper.getResultMaps();
        if (CollectionUtils.isEmpty(resultMaps)) {
            return null;
        }
        return resultMaps;
    }

    @Nullable
    private SqlMap getMapper(PsiElement position, Project project) {
        InjectedLanguageManager injectedLanguageManager = InjectedLanguageManager.getInstance(project);
        PsiFile topLevelFile = injectedLanguageManager.getTopLevelFile(position);
        if (!DomUtils.isIbatisFile(topLevelFile)) {
            return null;
        }
        int offset = injectedLanguageManager.injectedToHost(position, position.getTextOffset());
        IdDomElement idDomElement = SqlMapperUtils.findParentIdDomElement(topLevelFile.findElementAt(offset));
        if (idDomElement == null) {
            return null;
        }
        return DomUtil.getParentOfType(idDomElement, SqlMap.class, true);
    }

}
