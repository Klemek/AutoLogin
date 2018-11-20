package fr.klemek.autologin;

public enum OS {
    UNKOWN(),
    WINDOWS("win"),
    MACOSX("mac","darwin"),
    UNIX("nix","nux","aix"),
    SOLARIS("sunos")
    ;

    String[] matches;
    OS(String...matches){
        this.matches = matches;
    }
}
