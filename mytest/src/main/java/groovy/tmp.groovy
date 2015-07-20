package groovy

new File().eachLine {
    String [] line = it.split(",");
    if(line.length == 3){
        int search = line[1];
        int click = line[2];
        if(search>40 && (click/search)<0.4){
            println it;
        }
    }
}

