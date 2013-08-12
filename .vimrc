filetype on
ab #d #define
ab #i #include

syntax enable
"set nu
set hlsearch
set tabstop=4
"filetype plugin on
set mouse=a
set ai
"inoremap ( ()<Esc>i
noremap H ^
noremap L $
"inoremap ` <Esc>
inoremap jj <Esc>
noremap ?? "*p
inoremap ?? <Esc>"*p
"let Tlist_Compact_Format = 1
"let Tlist_Exit_OnlyWindow = 1
"let Tlist_GainFocus_On_ToggleOpen = 1
"let Tlist_Inc_Winwidth = 0
"let Tlist_Process_File_Always = 1
"let tlist_cpp_settings = 'c++;c:class;f:function;m:member;l:variable;p:private;s:structure;g:enum;a:array'
"let tlist_c_settings = 'c;c:class;f:function;m:member;l:variable;p:private;s:structure;g:enum;a:array;macro:macro'
"let winManagerWindowLayout = 'FileExplorer|TagList'
"set statusline=%<%f%=%([%{Tlist_Get_Tagname_By_Line()}]%)
map <C-J> <C-W>j
map <C-K> <C-W>k
map <C-L> <C-W>l
map <C-H> <C-W>h
"nmap <silent> <c-l> :TlistToggle<CR>
"map <C-\> :tab split<CR>:exec("tag ".expand("<cword>"))<CR>
nnoremap <C-\> <C-t>
set tags=./tags;
au BufRead,BufNewFile *.pdl set filetype=pdl
au! Syntax pdl source ~/.vim/pdl.vim
au BufRead,BufNewFile *.gdb set filetype=gdb
au! Syntax gdb source ~/.vim/gdb.vim
au BufRead,BufNewFile *.scala set filetype=scala
au! Syntax scala source ~/.vim/scala.vim
au FileType java set tags=~/.java_tags
set complete=.,w,b,u,t,i 
