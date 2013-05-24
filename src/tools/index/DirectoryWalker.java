package tools.index;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;

class DirectoryWalker extends SimpleFileVisitor<Path>
{
	private final PathMatcher matcher;
	private List<DirectoryListener> listeners = new LinkedList<DirectoryListener>();
	
    DirectoryWalker(String pattern)
    {
        matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
    }

    public void addListener(DirectoryListener listener)
    {
    	listeners.add(listener);    
    }
    
    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
    {
    	notifyListenersOfDirEntry(dir);
    	return FileVisitResult.CONTINUE;
    }
    
    private void notifyListenersOfDirEntry(Path dir)
    {
    	for(DirectoryListener listener : listeners){
			listener.preVisitDirectory(dir);
		}
	}

    @Override public FileVisitResult postVisitDirectory(Path dir, IOException exc)
    {
    	notifyListenersOfDirExit(dir);
    	return FileVisitResult.CONTINUE;
    }
    
	private void notifyListenersOfDirExit(Path dir)
	{
		for(DirectoryListener listener : listeners){
			listener.postVisitDirectory(dir);
		}
	}

	@Override public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
    {
    	    	
    	if(!fileMatches(file)){
    		return FileVisitResult.CONTINUE;
    	}
    	
     	notifyListenersOfFile(file);
     	return FileVisitResult.CONTINUE;
    }

	private void notifyListenersOfFile(Path filename)
	{
		for(DirectoryListener listener : listeners){
			listener.visitFile(filename);
		}
	}

	private boolean fileMatches(Path file)
	{
		Path name = file.getFileName();
        if(name == null)
        	return false;
        return matcher.matches(name);
	}
    

}