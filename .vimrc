filetype on
filetype plugin on
execute pathogen#infect()
let g:flake8_ignore="W191, E231, E501, E203, E226, E302, W291"

ab #d #define
ab #i #include

syntax enable
"set nu
set hlsearch
set tabstop=4
"filetype plugin on
set mouse=a
set ai

set bg=dark
"inoremap ( ()<Esc>i
noremap H ^
noremap L $
"inoremap ` <Esc>

inoremap jj <Esc>
inoremap <CAPSLOCK> <C>
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
map <F3>	<C-D>
map <F7>	<C-U>
"nmap <silent> <c-l> :TlistToggle<CR>
"map <C-\> :tab split<CR>:exec("tag ".expand("<cword>"))<CR>

nnoremap <C-\> <C-t>
au BufRead,BufNewFile *.pdl set filetype=pdl
au! Syntax pdl source ~/.vim/pdl.vim
au BufRead,BufNewFile *.gdb set filetype=gdb
au! Syntax gdb source ~/.vim/gdb.vim
"au BufRead,BufNewFile *.scala set filetype=scala
au BufRead,BufNewFile *.rkt set filetype=scheme
"au! Syntax scala source ~/.vim/scala.vim
"set complete=.,w,b,u,t,i 
let g:EclimProjectTreeAutoOpen=1
nmap <F8> :TagbarToggle<CR>
nmap <F3> :JavaSearch -a tabe<CR>
nmap <C-B> : CtrlPBuffer<CR>


  if has("autocmd")
    au BufReadPost *.rkt,*.rktl set filetype=racket
    au filetype racket set lisp
    au filetype racket set autoindent
  endif


"NERDTree
autocmd StdinReadPre * let s:std_in=1
autocmd VimEnter * if argc() == 0 && !exists("s:std_in") | NERDTree | endif
autocmd bufenter * if (winnr("$") == 1 && exists("b:NERDTreeType") && b:NERDTreeType == "primary") | q | endif
map <C-n> :NERDTreeToggle<CR>

" NERDTress File highlighting
 function! NERDTreeHighlightFile(extension, fg, bg, guifg, guibg)
  exec 'autocmd filetype nerdtree highlight ' . a:extension .' ctermbg='.  a:bg .' ctermfg='. a:fg .' guibg='. a:guibg .' guifg='. a:guifg
   exec 'autocmd filetype nerdtree syn match ' . a:extension .' #^\s\+.*'.  a:extension .'$#'
   endfunction

   call NERDTreeHighlightFile('jade', 'green', 'none', 'green', '#151515')
   call NERDTreeHighlightFile('ini', 'yellow', 'none', 'yellow', '#151515')
   call NERDTreeHighlightFile('md', 'blue', 'none', '#3366FF', '#151515')
   call NERDTreeHighlightFile('yml', 'yellow', 'none', 'yellow', '#151515')
   call NERDTreeHighlightFile('config', 'yellow', 'none', 'yellow', '#151515')
   call NERDTreeHighlightFile('conf', 'yellow', 'none', 'yellow', '#151515')
   call NERDTreeHighlightFile('json', 'yellow', 'none', 'yellow', '#151515')
   call NERDTreeHighlightFile('html', 'yellow', 'none', 'yellow', '#151515')
   call NERDTreeHighlightFile('styl', 'cyan', 'none', 'cyan', '#151515')
   call NERDTreeHighlightFile('css', 'cyan', 'none', 'cyan', '#151515')
   call NERDTreeHighlightFile('coffee', 'Red', 'none', 'red', '#151515')
   call NERDTreeHighlightFile('js', 'Red', 'none', '#ffa500', '#151515')
   call NERDTreeHighlightFile('php', 'Magenta', 'none', '#ff00ff', '#151515')



nmap }  gt
nmap {  gT
set tags=.tags;~
set runtimepath^=~/.vim/bundle/ctrlp.vim
let g:ctrlp_map = '<c-p>'
let g:ctrlp_buffer_map = '<c-b>'
let g:ctrlp_cmd = 'CtrlP'
let g:ctrlp_working_path_mode = 'cra'
let g:ctrlp_custom_ignore = {
	\ 'dir':  '\v[\/]\.(git|hg|svn)|bin$',
		\ 'file': '\v\.(exe|so|dll|class)$',
			\ 'link': 'SOME_BAD_SYMBOLIC_LINKS',
				\ }
