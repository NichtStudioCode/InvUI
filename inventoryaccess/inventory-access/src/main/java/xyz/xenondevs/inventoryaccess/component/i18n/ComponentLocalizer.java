package xyz.xenondevs.inventoryaccess.component.i18n;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

abstract class ComponentLocalizer<C> {
    
    private static final Pattern FORMAT_PATTERN = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");
    
    public abstract C localize(String lang, C component);
    
    protected abstract C createTextComponent(String text);
    
    protected List<C> decomposeFormatString(String lang, String formatString, C component, List<C> args) {
        var matcher = FORMAT_PATTERN.matcher(formatString);
        
        var components = new ArrayList<C>();
        var sb = new StringBuilder();
        var nextArgIdx = 0;
        
        var i = 0;
        while (matcher.find(i)) {
            var start = matcher.start();
            var end = matcher.end();
            
            // check for escaped %
            var matchedStr = formatString.substring(i, start);
            if ("%%".equals(matchedStr)) {
                sb.append('%');
            } else {
                // check for invalid format, only %s is supported
                var argType = matcher.group(2);
                if (!"s".equals(argType)) {
                    throw new IllegalStateException("Unsupported format: '" + matchedStr + "'");
                }
                
                // retrieve argument index
                var argIdxStr = matcher.group(1);
                var argIdx = argIdxStr == null ? nextArgIdx++ : Integer.parseInt(argIdxStr) - 1;
                
                // validate argument index
                if (argIdx < 0)
                    throw new IllegalStateException("Invalid argument index: " + argIdx);
                
                // append the text before the argument
                sb.append(formatString, i, start);
                // add text component
                components.add(createTextComponent(sb.toString()));
                // add argument component
                components.add(args.size() <= argIdx ? createTextComponent("") : localize(lang, args.get(argIdx)));
                // clear string builder
                sb.setLength(0);
            }
            
            // start next search after matcher end index
            i = end;
        }
        
        // append the text after the last argument
        if (i < formatString.length()) {
            sb.append(formatString, i, formatString.length());
            components.add(createTextComponent(sb.toString()));
        }
        
        return components;
    }
    
}
