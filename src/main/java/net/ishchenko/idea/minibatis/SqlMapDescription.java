package net.ishchenko.idea.minibatis;

import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomFileDescription;
import net.ishchenko.idea.minibatis.model.sqlmap.SqlMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 24.12.11
 * Time: 0:00
 */
public class SqlMapDescription extends DomFileDescription<SqlMap> {

    public SqlMapDescription() {
        super(SqlMap.class, "sqlMap");
    }

    @Override
    public boolean isMyFile(@NotNull XmlFile file, @Nullable Module module) {
        if (!isXmlFile(file)) {
            return false;
        }
        XmlTag rootTag = file.getRootTag();
        return null != rootTag && rootTag.getName().equals("sqlMap");
    }

    static boolean isXmlFile(@NotNull PsiFile file) {
        return file instanceof XmlFile;
    }
}
